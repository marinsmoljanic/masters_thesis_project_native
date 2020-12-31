(ns app.controllers.user.member-id-card
  (:require [keechma.next.controller :as ctrl]
            [keechma.next.controllers.pipelines :as pipelines]
            [keechma.next.controllers.entitydb :as edb]
            [keechma.next.toolbox.logging :as l]
            [app.api :as api]
            [oops.core :refer [ocall oget oset!]]
            [app.settings :refer [api-key]]
            [promesa.core :as p]
            [keechma.pipelines.core :as pp :refer-macros [pipeline!]]))


(derive :member-id-card ::pipelines/controller)

(def load-pdf
  (-> (pipeline! [value {:keys [deps-state* meta-state*] :as ctrl}]
                 (let [jwt (:jwt @deps-state*)
                       member-id (:member-id @deps-state*)]
                   (pipeline! [value {:keys [meta-state*] :as ctrl}]
                              (api/get-member-id-card {:jwt jwt :data {:apikey api-key
                                                                       :memberrecid member-id
                                                                       :card_side "FRONT"}})
                              (if (:error value)
                                (throw (ex-info "Error member id card" value))
                                (ctrl/dispatch ctrl :member-id-card :create-object-url-front value))

                              (api/get-member-id-card {:jwt jwt :data {:apikey api-key
                                                                       :memberrecid member-id
                                                                       :card_side "BACK"}})
                              (if (:error value)
                                (throw (ex-info "Error member id card" value))
                                (ctrl/dispatch ctrl :member-id-card :create-object-url-back value))
                              
                              (rescue! [error]
                                    (ctrl/dispatch ctrl :be-error-handler :resolve-error (ex-data error))))))
      (pp/set-queue :load-pdf)
      pp/use-existing))

(def pipelines
  {:keechma.on/start load-pdf
   :create-object-url-front (pipeline! [value {:keys [state*] :as ctrl}]
                                 (let [file-reader (oget js/window :FileReader)
                                       new-file-reader (file-reader.)]
                                   (ocall new-file-reader :readAsDataURL value)
                                   (oset! new-file-reader :onloadend #(ctrl/transact ctrl (fn [] (pp/swap! state* assoc :base64-front (oget new-file-reader :_result)))))))
   :create-object-url-back (pipeline! [value {:keys [state*] :as ctrl}]
                                 (let [file-reader (oget js/window :FileReader)
                                       new-file-reader (file-reader.)]
                                   (ocall new-file-reader :readAsDataURL value)
                                   (oset! new-file-reader :onloadend #(ctrl/transact ctrl (fn [] (pp/swap! state* assoc :base64-back (oget new-file-reader :_result)))))))})

(defmethod ctrl/prep :member-id-card [ctrl]
  (pipelines/register ctrl pipelines))

(defmethod ctrl/start :member-id-card [_ _ _ _]
  {:base64-front nil
   :base64-back nil})

(defmethod ctrl/derive-state :member-id-card [_ state {:keys [entitydb]}]
  {:front (:base64-front state)
   :back  (:base64-back state)})
