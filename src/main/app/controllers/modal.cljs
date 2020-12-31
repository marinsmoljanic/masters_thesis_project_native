(ns app.controllers.modal
  (:require [keechma.next.controller :as ctrl]
            [app.domain.modal-data :as md]
            [keechma.next.controllers.pipelines :as pipelines]
            [keechma.pipelines.core :as pp :refer-macros [pipeline!]]))

(derive :modal ::pipelines/controller)

(defn close-modal [state*]
  (pp/reset! state* {:open false
                     :data nil
                     :cb nil}))

(defn close-modal-with-cb [state* cb] ;execute callback and close modal
  (pipeline! [_ _]
             (close-modal state*)
             (cb)))

(defn resolve-modal-with-cb [state* type cb] ; get modal data, open modal, store cb in state. Cb will be executed on close.
  (pipeline! [value {:keys [state*]}]
             (md/get-modal-data type)
             (pp/reset! state* {:open true
                                :data value
                                :cb cb})))

(defn resolve-modal [state* type cb]
  (pipeline! [value {:keys [state*]}]
             (md/get-modal-data type)
             (pp/reset! state* {:open true
                                :data value
                                :cb cb})))

(def pipelines
  {:keechma.on/start (pipeline! [_ {:keys [state*]}]
                                (pp/reset! state* {:open false
                                                   :data nil
                                                   :cb nil}))

   :open  (pipeline! [value {:keys [state*]}]
                     (if-let [cb (get value :cb)]
                       (resolve-modal-with-cb state* (:type value) cb)
                       (resolve-modal state* value nil)))

   :close (pipeline! [_ {:keys [state*]}]
                     (if-let [cb (get @state* :cb)]
                       (close-modal-with-cb state* cb)
                       (close-modal state*)))})

(defmethod ctrl/prep :modal [ctrl]
  (-> ctrl
      (pipelines/register pipelines)))

(defmethod ctrl/derive-state :modal [_ state _]
  state)
