(ns app.controllers.current-user
  (:require [keechma.next.controller :as ctrl]
            [keechma.next.controllers.pipelines :as pipelines]
            [keechma.next.controllers.entitydb :as edb]
            [keechma.next.toolbox.logging :as l]
            [app.api :as api]
            [app.settings :refer [api-key]]
            [keechma.pipelines.core :as pp :refer-macros [pipeline!]]))

(derive :current-user ::pipelines/controller)

(def load-user
  (-> (pipeline! [value {:keys [deps-state* meta-state*] :as ctrl}]
                 (let [jwt (:jwt @deps-state*)
                       member-id (:member-id @deps-state*)]
                   (when (and jwt member-id)
                     (pipeline! [value {:keys [meta-state*]}]
                                (api/get-current-user {:jwt jwt :data {:apikey api-key
                                                                       :memberrecid member-id}})
                                (if (seq (select-keys value [:issues]))
                                  (throw (ex-info "Error" value))
                                  (edb/insert-named! ctrl :entitydb :user :user/current value))

                                (rescue! [error]
                                         (ctrl/dispatch ctrl :be-error-handler :resolve-error (ex-data error)))))))
      (pp/set-queue :load-user)
      pp/use-existing))

(def pipelines
  {:keechma.on/start load-user
   :keechma.on/deps-change (pipeline! [value ctrl]
                                      (when (or (contains? value :jwt) (contains? value :member-id))
                                        (if (or (:jwt value) (:member-id value))
                                          load-user
                                          (edb/remove-named! ctrl :entitydb :user/current))))
   :keechma.on/stop (pipeline! [_ ctrl]
                                (edb/remove-named! ctrl :entitydb :user/current))})

(defmethod ctrl/prep :current-user [ctrl]
  (pipelines/register ctrl pipelines))

(defmethod ctrl/derive-state :current-user [_ state {:keys [entitydb]}]
   (edb/get-named entitydb :user/current))