(ns app.controllers.forms.forgot-password
  (:require [keechma.next.controller :as ctrl]
            [keechma.next.controllers.pipelines :as pipelines]
            [keechma.pipelines.core :as pp :refer-macros [pipeline!]]
            [keechma.next.controllers.form :as form]
            [keechma.next.controllers.router :as router]
            [app.validators :as v]
            [app.settings :refer [api-key]]
            [app.api :as api]))

(derive :forgot-password-form ::pipelines/controller)

(defn prepare-data [data]
  {:apikey api-key
   :usernameemail (:email data)})

(def pipelines
  {:keechma.form/submit-data
   (pipeline! [value {:keys [meta-state* deps-state*] :as ctrl}]
              (let [data (prepare-data value)]
                (pipeline! [value {:keys [state*] :as ctrl}]
                           (api/forgot-password data)
                           (if (seq (select-keys value [:issues]))
                             (throw (ex-info "Error" value))
                             (ctrl/dispatch ctrl :router :redirect "email-sent"))
                           (rescue! [error]
                                   (pp/reset! state* {:error (ex-data error)})))))})

(defmethod ctrl/prep :forgot-password-form [ctrl]
  (pipelines/register ctrl
                      (form/wrap pipelines
                                 (v/to-validator {:email [:email :not-empty]}))))
