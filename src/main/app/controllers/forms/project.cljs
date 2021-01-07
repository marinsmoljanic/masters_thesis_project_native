(ns app.controllers.forms.project
  (:require [keechma.next.controller :as ctrl]
            [keechma.next.controllers.pipelines :as pipelines]
            [keechma.pipelines.core :as pp :refer-macros [pipeline!]]
            [keechma.next.controllers.form :as form]
            [keechma.next.controllers.router :as router]
            [app.validators :as v]
            [app.rn.navigation :refer [navigate]]
            [app.settings :refer [api-key]]
            [app.gql :refer [m!]]
            [app.api :as api]))

(derive :project-form ::pipelines/controller)

(def pipelines
  {:keechma.form/submit-data (pipeline! [value {:keys [state*] :as ctrl}]

                                        (m! [:create-project [:createProject]] {:name         (:name        value)
                                                                                :description  (:description value)
                                                                                :startDate    (:startDate   value)
                                                                                :endDate      (:endDate     value)})

                                        (ctrl/dispatch ctrl :router :redirect "project"))})

(defmethod ctrl/prep :project-form [ctrl]
           (pipelines/register ctrl
                               (form/wrap pipelines
                                          (v/to-validator {:name        [:not-empty]
                                                           :description [:not-empty]
                                                           :startDate   [:not-empty]
                                                           :endDate     [:not-empty]}))))