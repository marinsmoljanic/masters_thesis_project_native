(ns app.controllers.forms.role
  (:require [keechma.next.controller :as ctrl]
            [keechma.next.controllers.pipelines :as pipelines]
            [keechma.pipelines.core :as pp :refer-macros [pipeline!]]
            [keechma.next.controllers.form :as form]
            [keechma.next.controllers.router :as router]
            [app.gql :refer [m!]]
            [app.validators :as v]))

(derive :role-form ::pipelines/controller)

(def pipelines
  {:keechma.form/submit-data (pipeline! [value {:keys [deps-state*] :as ctrl}]
                                        (m! [:create-role [:createRole]] {:Name (:Name value)})
                                        (ctrl/dispatch ctrl :router :redirect "onboarding"))})

(defmethod ctrl/start :role-form [_ state _ _]
           {:is-project-form-open? nil})

(defmethod ctrl/prep :role-form [ctrl]
           (pipelines/register ctrl
                               (form/wrap pipelines
                                          (v/to-validator {:Name     [:not-empty]}))))