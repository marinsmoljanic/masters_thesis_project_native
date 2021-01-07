(ns app.controllers.forms.person
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

(derive :person-form ::pipelines/controller)

(def pipelines
  {:keechma.form/submit-data (pipeline! [value {:keys [state*] :as ctrl}]
                                           (println "Ispis iz kontrolera" value)
                                           (m! [:create-person [:createPerson]] {:firstName (:firstName   value)
                                                                                   :lastName  (:lastName    value)
                                                                                   :personalId (:personalId value)})

                                           (ctrl/dispatch ctrl :router :redirect "person"))})

(defmethod ctrl/prep :person-form [ctrl]
           (pipelines/register ctrl
                               (form/wrap pipelines
                                          (v/to-validator {:firstName  [:not-empty]
                                                           :lastName   [:not-empty]
                                                           :personalId [:not-empty]}))))