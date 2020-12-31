(ns app.controllers.be-error-handler
  (:require [keechma.next.controller :as ctrl]
            [keechma.next.controllers.pipelines :as pipelines]
            [keechma.pipelines.core :as pp :refer-macros [pipeline!]]))

(derive :be-error-handler ::pipelines/controller)

(def handle-expired-jwt
  (pipeline! [_ ctrl]
    (ctrl/dispatch ctrl :modal :open {:type :expired-jwt
                                      :cb #(ctrl/dispatch ctrl :jwt :log-out)})))


(def pipelines
  {:resolve-error 
   (pipeline! [value ctrl]
              (cond
                (= "Expired token" (get-in value [:value :response :original :issues :issuemessage])) handle-expired-jwt
                :default (ctrl/dispatch ctrl :modal :open :error)))})

(defmethod ctrl/prep :be-error-handler [ctrl]
  (-> ctrl
      (pipelines/register pipelines)))

(defmethod ctrl/derive-state :be-error-handler [_ state _]
  state)
