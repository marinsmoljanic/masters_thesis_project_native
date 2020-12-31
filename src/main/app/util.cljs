(ns app.util)

(defn resolve-error [form-meta]
  (let [form (:keechma.next.controllers.form/form form-meta)
        state (get-in form [:state :type])
        error (get-in form [:state :data])
        message (:message (ex-data error))
        issues (:issues (ex-data error))
        error? (= state :error)]
    (when error?
      {:message message
       :issues issues})))