(ns app.keechma-app
  (:require [app.controllers.router]
            [app.controllers.app-state]

            [app.controllers.datasources.role]
            [app.controllers.datasources.person]
            [app.controllers.datasources.project]
            [app.controllers.datasources.person-role]
            [app.controllers.datasources.person-role-by-personid]
            [app.controllers.datasources.person-role-by-projectid]

            [app.controllers.forms.role]
            [app.controllers.forms.person]
            [app.controllers.forms.project]
            [app.controllers.forms.role-edit]
            [app.controllers.forms.person-edit]
            [app.controllers.forms.project-edit]))

(def app
  {:keechma/controllers {:router                   #:keechma.controller {:params true}

                         :app-state                #:keechma.controller {:params true}

                         :entitydb                 {:keechma.controller/params true
                                                    :keechma.controller/type   :keechma/entitydb
                                                    :keechma.entitydb/schema   {:user      {:entitydb/id :member_record_id}
                                                                                :providers {:entitydb/id :telemedproviderid}}}

                         ;; PERSON
                         :persons                  #:keechma.controller {:params (fn [{:keys [router]}]
                                                                                     (or (:is-active (get (:routes router) [:person]))
                                                                                         (:is-active (get (:routes router) [:project-edit]))))
                                                                         :deps   [:router :entitydb]}

                         :person-form              #:keechma.controller {:params (fn [{:keys [router]}]
                                                                                     (:is-active (get (:routes router) [:person-add])))
                                                                         :deps   [:router :entitydb]}

                         :person-edit-form         #:keechma.controller {:params (fn [{:keys [router]}]
                                                                                     (:is-active (get (:routes router) [:person-edit])))
                                                                         :deps   [:router :entitydb]}

                         ;; PROJECT
                         :projects                 #:keechma.controller {:params (fn [{:keys [router]}]
                                                                                     (or (:is-active (get (:routes router) [:project]))
                                                                                         (:is-active (get (:routes router) [:person-edit]))))
                                                                         :deps   [:router :entitydb]}

                         :project-form             #:keechma.controller {:params (fn [{:keys [router]}]
                                                                                     (:is-active (get (:routes router) [:project-add])))
                                                                         :deps   [:router :entitydb]}

                         :project-edit-form        #:keechma.controller {:params (fn [{:keys [router]}]
                                                                                     (:is-active (get (:routes router) [:project-edit])))
                                                                         :deps   [:router :entitydb]}


                         ;; ROLE
                         :roles                    #:keechma.controller {:params (fn [{:keys [router]}]
                                                                                     (or (:is-active (get (:routes router) [:role]))
                                                                                         (:is-active (get (:routes router) [:project-edit]))
                                                                                         (:is-active (get (:routes router) [:person-edit]))))
                                                                         :deps   [:router :entitydb]}

                         [:role-edit-form]         {:keechma.controller.factory/produce
                                                                             (fn [{:keys [roles]}]
                                                                                 (->> (map (fn [role] [(:id role) {:keechma.controller/params role}])
                                                                                           roles)
                                                                                      (into {})))
                                                    :keechma.controller/deps [:roles]}

                         :role-form                #:keechma.controller {:params (fn [{:keys [router]}]
                                                                                     (or (:is-active (get (:routes router) [:role]))
                                                                                         (:is-active (get (:routes router) [:person-edit]))))
                                                                         :deps   [:router :entitydb]}

                         ;; UTILITY
                         :person-role-by-personid  #:keechma.controller {:params (fn [{:keys [router]}]
                                                                                     (:is-active (get (:routes router) [:person-edit])))
                                                                         :deps   [:router :entitydb :roles :projects]}

                         :person-role-by-projectid #:keechma.controller {:params (fn [{:keys [router]}]
                                                                                     (:is-active (get (:routes router) [:project-edit])))
                                                                         :deps   [:router :entitydb :roles :persons]}

                         }})
