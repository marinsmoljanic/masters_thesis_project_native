(ns app.controllers.forms.signin
  (:require [keechma.next.controller :as ctrl]
            [keechma.next.controllers.pipelines :as pipelines]
            [keechma.pipelines.core :as pp :refer-macros [pipeline!]]
            [keechma.next.controllers.form :as form]
            [keechma.next.controllers.router :as router]
            [app.validators :as v]
            [app.rn.navigation :refer [navigate]]
            [app.settings :refer [api-key]]
            [app.api :as api]))

(derive :sign-in-form ::pipelines/controller)

(defn prepare-data [data]
  {:apikey api-key
   :emailaddress (:email data)
   :password (:password data)})

(def pipelines
  {:keechma.form/submit-data
   (pipeline! [value {:keys [meta-state* deps-state*] :as ctrl}]
              (let [data (prepare-data value)]
                (pipeline! [value {:keys [state*] :as ctrl}]
                           (api/signin data)
                           (if (seq (select-keys value [:issues]))
                             (throw (ex-info "Error" value))
                             (pipeline! [value {:keys [state*] :as ctrl}]
                               (ctrl/dispatch ctrl :jwt :set-jwt value)
                               (ctrl/dispatch ctrl :router :redirect "home")))
                           
                           (rescue! [error]
                                   (pp/reset! state* {:error (ex-data error)})))))})

(defmethod ctrl/prep :sign-in-form [ctrl]
  (pipelines/register ctrl
                      (form/wrap pipelines
                                 (v/to-validator {:email    [:email :not-empty]
                                                  :password [:not-empty :password?]}))))