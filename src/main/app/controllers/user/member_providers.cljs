(ns app.controllers.user.member-providers
  (:require [keechma.next.controller :as ctrl]
            [keechma.next.controllers.pipelines :as pipelines]
            [keechma.next.controllers.entitydb :as edb]
            [keechma.next.toolbox.logging :as l]
            [app.api :as api]
            [oops.core :refer [ocall oget oset!]]
            [app.settings :refer [api-key]]
            [promesa.core :as p]
            [keechma.pipelines.core :as pp :refer-macros [pipeline!]]))


(derive :member-providers ::pipelines/controller)

(def load-providers
  (-> (pipeline! [value {:keys [deps-state* meta-state*] :as ctrl}]
                 (let [jwt (:jwt @deps-state*)
                       member-id (:member-id @deps-state*)]
                   (pipeline! [value {:keys [meta-state*] :as ctrl}]
                              (api/get-member-providers {:jwt jwt :data {:apikey api-key
                                                                         :memberrecid member-id}})
                              (if (:error value)
                                (throw (ex-info "Error" value))
                                (edb/insert-collection! ctrl :entitydb :providers :providers/list value))
                              (rescue! [error]
                                       (ctrl/dispatch ctrl :be-error-handler :resolve-error (ex-data error))))))
      (pp/set-queue :load-member-providers)
      pp/use-existing))

(def pipelines
  {:keechma.on/start load-providers})

(defmethod ctrl/prep :member-providers [ctrl]
  (pipelines/register ctrl pipelines))

(defmethod ctrl/derive-state :member-providers [_ state {:keys [entitydb]}]
  (edb/get-collection entitydb :providers/list))
