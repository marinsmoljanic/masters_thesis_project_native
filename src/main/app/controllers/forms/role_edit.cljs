(ns app.controllers.forms.role-edit
  (:require [keechma.next.controller :as ctrl]
            [keechma.next.controllers.pipelines :as pipelines]
            [keechma.pipelines.core :as pp :refer-macros [pipeline!]]
            [keechma.next.controllers.form :as form]
            [keechma.next.controllers.router :as router]
            [keechma.next.controllers.entitydb :as edb]
            [app.gql :refer [m!]]
            [app.validators :as v]))

(derive :role-edit-form ::pipelines/controller)

(def pipelines
  {:keechma.form/get-data    (pipeline! [value {:keys [deps-state*] :as ctrl}]
                                        value)

   :keechma.form/submit-data (pipeline! [value {:keys [deps-state*] :as ctrl}]
                                        (println "VALUE from controler: " value)
                                        (m! [:update-role [:updateRole]] {:id (:id value)
                                                                          :name (:Name value)})
                                        (ctrl/dispatch ctrl :router :redirect "onboarding"))
   :delete                   (pipeline! [value {:keys [deps-state*] :as ctrl}]
                                        (m! [:delete-role [:deleteRole]] {:id value})
                                        (ctrl/dispatch ctrl :router :redirect "onboarding"))})

(defmethod ctrl/prep :role-edit-form [ctrl]
           (pipelines/register ctrl
                               (form/wrap pipelines
                                          (v/to-validator {}))))
