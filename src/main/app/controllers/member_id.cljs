(ns app.controllers.member-id
  (:require [keechma.next.controller :as ctrl]
            [keechma.next.controllers.pipelines :as pipelines]
            [keechma.pipelines.core :as pp :refer-macros [pipeline!]]
            [keechma.next.controllers.form :as form]
            [oops.core :refer [ocall oget oset!]]
            [keechma.next.controllers.router :as router]
            [app.validators :as v]
            ["@react-native-async-storage/async-storage" :default AsyncStorage]))

(derive :member-id ::pipelines/controller)

(def pipelines
  {:set-member-id
   (pipeline! [value {:keys [state*] :as ctrl}]
              (ocall AsyncStorage :setItem "@memberrecid" value)
              (pp/reset! state* value)
              (rescue! [error]
                       (pp/reset! state* nil)))

   :keechma.on/start
   (pipeline! [value {:keys [state*] :as ctrl}]
              (ocall AsyncStorage :getItem "@memberrecid")
              (pp/reset! state* (if (true? value) nil value))
              (rescue! [error]
                       (pp/reset! state* nil)))})

(defmethod ctrl/prep :member-id [ctrl]
  (-> ctrl
      (pipelines/register pipelines)))

(defmethod ctrl/derive-state :member-id [_ state _]
  state)