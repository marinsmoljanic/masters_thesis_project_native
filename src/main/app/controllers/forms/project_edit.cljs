(ns app.controllers.forms.project-edit
  (:require [keechma.next.controller :as ctrl]
            [keechma.next.controllers.pipelines :as pipelines]
            [keechma.pipelines.core :as pp :refer-macros [pipeline!]]
            [keechma.next.controllers.form :as form]
            [keechma.next.controllers.router :as router]
            [keechma.next.toolbox.logging :as l]
            [app.gql :refer [m!]]
            [app.validators :as v]))

(derive :project-edit-form ::pipelines/controller)

(def pipelines
  {:keechma.form/get-data    (pipeline! [_ {:keys [deps-state*]}]
                                        (let [data (get-in @deps-state* [:router :routes [:project-edit] :params])
                                              name (:name data)
                                              description (:description data)]
                                              {:name        name
                                               :description description}))

   :delete-project            (pipeline! [value {:keys [deps-state*] :as ctrl}]
                                         (m! [:delete-project [:deleteProject]] {:id (get-in @deps-state* [:router :routes [:project-edit] :params :id])})
                                         (ctrl/dispatch ctrl :router :redirect "project"))

   :keechma.form/submit-data (pipeline! [value {:keys [deps-state*] :as ctrl}]
                                        (m! [:update-project [:updateProject]] {:id          (get-in @deps-state* [:router :routes [:project-edit] :params :id])
                                                                                :name        (:name value)
                                                                                :description (:description value)
                                                                                :startDate   (get-in @deps-state* [:router :routes [:project-edit] :params :startDate])
                                                                                :endDate     (get-in @deps-state* [:router :routes [:project-edit] :params :endDate])})
                                        (ctrl/dispatch ctrl :router :redirect "project"))})

(defmethod ctrl/prep :project-edit-form [ctrl]
           (pipelines/register ctrl
                               (form/wrap pipelines
                                          (v/to-validator {:name        [:not-empty]
                                                           :description [:not-empty]}))))

(defmethod ctrl/derive-state :project-edit-form [_ _ deps-state]
           {:project-edit (get-in deps-state [:router :routes [:project-edit] :params])})