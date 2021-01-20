(ns app.controllers.forms.person-role-edit
  (:require [keechma.next.controller :as ctrl]
            [keechma.next.controllers.pipelines :as pipelines]
            [keechma.pipelines.core :as pp :refer-macros [pipeline!]]
            [keechma.next.controllers.form :as form]
            [keechma.next.controllers.router :as router]
            [app.gql :refer [m!]]
            [app.validators :as v]))

(derive :person-role-edit-form ::pipelines/controller)

(def pipelines
  {:keechma.form/submit-data (pipeline! [value {:keys [deps-state*] :as ctrl}]
                                        (m! [:update-person-role [:updatePersonRole]] {:oldProject (get-in @deps-state* [:router :person-role])
                                                                                       :project    (:project value)
                                                                                       :role       (:role value)
                                                                                       :date       (:date value)}))
   :delete                   (pipeline! [value {:keys [deps-state*] :as ctrl}]
                                        (m! [:delete-person-role [:deletePersonRole]] {:personrole (str (get-in @deps-state* [:router :person-role]))}))})

(defmethod ctrl/start :person-role-edit-form [_ state _ _]
           {:is-form-open? nil})

(defmethod ctrl/prep :person-role-edit-form [ctrl]
           (pipelines/register ctrl
                               (form/wrap pipelines
                                          (v/to-validator {}))))
