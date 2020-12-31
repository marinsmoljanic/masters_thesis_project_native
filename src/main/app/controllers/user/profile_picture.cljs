(ns app.controllers.user.profile-picture
  (:require [keechma.next.controller :as ctrl]
            [keechma.next.controllers.pipelines :as pipelines]
            [keechma.next.controllers.entitydb :as edb]
            [keechma.next.toolbox.logging :as l]
            [app.api :as api]
            [oops.core :refer [ocall oget oset!]]
            [app.settings :refer [api-key]]
            [promesa.core :as p]
            [keechma.pipelines.core :as pp :refer-macros [pipeline!]]))


(derive :profile-picture ::pipelines/controller)

(def load-picture
  (-> (pipeline! [value {:keys [deps-state* meta-state*] :as ctrl}]
                 (let [jwt (:jwt @deps-state*)
                       member-id (:member-id @deps-state*)]
                   (pipeline! [value {:keys [state*] :as ctrl}]
                              (api/get-member-profile-picture {:jwt jwt :data {:apikey api-key
                                                                               :memberrecid member-id}})
                              (if (:error value)
                                (throw (ex-info "Error" value))
                                (pp/swap! state* assoc :base-64 (str "data:image/png;base64," (:profilepicture value))))

                              (rescue! [error]
                                       (ctrl/dispatch ctrl :be-error-handler :resolve-error (ex-data error))))))
      (pp/set-queue :load-picture)
      pp/use-existing))

(def upload-picture
  (-> (pipeline! [value {:keys [state* deps-state*] :as ctrl}]
                 (let [uri (:uri @state*)
                       ext (second (clojure.string/split uri #"\."))
                       member-id (:member-id @deps-state*)
                       base-64 (:base-64 @state*)
                       jwt (:jwt @deps-state*)
                       data {:jwt jwt
                             :data {:apikey api-key
                                    :memberrecid member-id
                                    :photo base-64
                                    :photo_ext ext}}]
                   (pipeline! [value _]
                              (api/update-member-profile-picture data)
                              (rescue! [error]
                                       (let [status (-> error ex-data :value :status)]
                                         (when-not (= 200 status)
                                           (ctrl/dispatch ctrl :error-handler :throw-error)))))))
      (pp/set-queue :upload-picture)
      pp/use-existing))

(def pipelines
  {:keechma.on/start load-picture

   :keechma.on/deps-change (pipeline! [value {:keys [state*] :as ctrl}]
                                      (when (contains? value :router)
                                        load-picture)
                                      (when (contains? value :image-upload)
                                        (pp/swap! state* assoc :uri (:uri (:image-upload value)))
                                        (pp/swap! state* assoc :base-64 (:base-64 (:image-upload value)))
                                        (ctrl/dispatch ctrl :profile-picture :upload)))
   
   :upload upload-picture})

(defmethod ctrl/start :profile-picture [_ _ _ _]
  {:uri nil
   :base-64 nil})

(defmethod ctrl/prep :profile-picture [ctrl]
  (pipelines/register ctrl pipelines))

(defmethod ctrl/derive-state :profile-picture [_ state {:keys [entitydb]}]
  state)
