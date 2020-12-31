(ns app.controllers.user.member-group
  (:require [keechma.next.controller :as ctrl]
            [keechma.next.controllers.pipelines :as pipelines]
            [keechma.next.controllers.entitydb :as edb]
            [keechma.next.toolbox.logging :as l]
            [app.api :as api]
            [oops.core :refer [ocall oget oset!]]
            [app.settings :refer [api-key]]
            [promesa.core :as p]
            [keechma.pipelines.core :as pp :refer-macros [pipeline!]]))


(derive :member-group ::pipelines/controller)

(def load-member-group
  (-> (pipeline! [value {:keys [deps-state* meta-state*] :as ctrl}]
                 (let [jwt (:jwt @deps-state*)
                       member-id (:member-id @deps-state*)]
                   (pipeline! [value {:keys [meta-state*] :as ctrl}]
                              (api/get-member-group {:jwt jwt :data {:apikey api-key
                                                                     :memberrecid member-id}})
                              (if (:error value)
                                (throw (ex-info "Error" value))
                                (edb/insert-named! ctrl :entitydb :group :group/current value))
                              
                              (rescue! [error]
                                    (ctrl/dispatch ctrl :be-error-handler :resolve-error (ex-data error))))))
      (pp/set-queue :load-member-group)
      pp/use-existing))

(def pipelines
  {:keechma.on/start load-member-group})

(defmethod ctrl/prep :member-group [ctrl]
  (pipelines/register ctrl pipelines))

(defmethod ctrl/derive-state :member-group [_ state {:keys [entitydb]}]
  (edb/get-named entitydb :group/current))
