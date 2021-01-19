(ns app.controllers.forms.person-edit
  (:require [keechma.next.controller :as ctrl]
            [keechma.next.controllers.pipelines :as pipelines]
            [keechma.pipelines.core :as pp :refer-macros [pipeline!]]
            [keechma.next.controllers.form :as form]
            [keechma.next.controllers.router :as router]
            [keechma.next.toolbox.logging :as l]
            [app.gql :refer [m!]]
            [app.validators :as v]))

(derive :person-edit-form ::pipelines/controller)

(def pipelines
  {:keechma.form/get-data    (pipeline! [value {:keys [deps-state*] :as ctrl}]
                                        (let [data (get-in @deps-state* [:router :routes [:person-edit] :params])
                                              first-name (:firstName data)
                                              last-name (:lastName data)]
                                              {:firstName first-name
                                               :lastName  last-name}))

   :delete-person            (pipeline! [value {:keys [deps-state*] :as ctrl}]
                                        (m! [:delete-person [:deletePerson]] {:id (get-in @deps-state* [:router :routes [:person-edit] :params :id])})
                                        (ctrl/dispatch ctrl :router :redirect "person"))

   :keechma.form/submit-data (pipeline! [value {:keys [deps-state*] :as ctrl}]
                                        (m! [:update-person [:updatePerson]] {:id        (get-in @deps-state* [:router :routes [:person-edit] :params :id])
                                                                              :firstName (:firstName value)
                                                                              :lastName  (:lastName value)})
                                        (ctrl/dispatch ctrl :router :redirect "person"))})

(defmethod ctrl/start :person-edit-form [_ state _ _]
           {:is-form-open? nil})

(defmethod ctrl/prep :person-edit-form [ctrl]
           (pipelines/register ctrl
                               (form/wrap pipelines
                                          (v/to-validator {:firstName  [:not-empty]
                                                           :lastName   [:not-empty]}))))
