(ns app.keechma-app
  (:require [app.controllers.router]
            [app.controllers.forms.register]
            [app.controllers.forms.signin]
            [app.controllers.forms.forgot-password]
            [app.controllers.image-upload]
            [app.controllers.current-user]
            [app.controllers.member-id]
            [app.controllers.forms.update-password]
            [app.controllers.user.member-id-card]
            [app.controllers.user.member-providers]
            [app.controllers.user.member-group]
            [app.controllers.modal]
            [app.controllers.user.profile-picture]
            [app.controllers.jwt]
            [app.controllers.app-state]
            [app.controllers.be-error-handler]))

(defn active-screen-params [screen]
  (fn [{:keys [router]}] (:is-active (get (:routes router) screen))))

(def app
  {:keechma/controllers {:router               #:keechma.controller {:params true}
                         
                         :app-state            #:keechma.controller {:params true}

                         :entitydb             {:keechma.controller/params true
                                                :keechma.controller/type   :keechma/entitydb
                                                :keechma.entitydb/schema   {:user      {:entitydb/id :member_record_id}
                                                                            :providers {:entitydb/id :telemedproviderid}}}

                         :image-upload         #:keechma.controller {:params (fn [{:keys [router]}] 
                                                                              (or (:is-active (get (:routes router) [:register]))
                                                                                  (:is-active (get (:routes router) [:home]))
                                                                                  (:is-active (get (:routes router) [:account-overview]))))
                                                                     :deps   [:router]}

                         :register-form        #:keechma.controller {:params (active-screen-params [:register])
                                                                     :deps   [:router :image-upload]}

                         :sign-in-form         #:keechma.controller {:params (active-screen-params [:signin])
                                                                     :deps   [:router]}

                         :forgot-password-form #:keechma.controller {:params (active-screen-params [:forgot-password])
                                                                     :deps   [:router]}

                         :update-password-form #:keechma.controller {:params (active-screen-params [:password-settings])
                                                                     :deps   [:router :current-user :jwt]}

                         :jwt                  #:keechma.controller {:params true
                                                                     :deps   [:router]}

                         :current-user         #:keechma.controller {:params (fn [{:keys [jwt]}] jwt)
                                                                     :deps   [:jwt :entitydb :member-id]}

                         :member-id            #:keechma.controller {:params true}

                         :member-id-card       #:keechma.controller {:params (active-screen-params [:flexcare-id-card])
                                                                     :deps   [:router :jwt :member-id]}

                         :member-providers     #:keechma.controller {:params (fn [{:keys [router]}]
                                                                               (or (:is-active (get (:routes router) [:plan-information]))
                                                                                   (:is-active (get (:routes router) [:home]))))
                                                                     :deps   [:router :jwt :member-id :entitydb]}

                         :member-group         #:keechma.controller {:params (active-screen-params [:plan-information])
                                                                     :deps   [:router :jwt :member-id :entitydb]}
                         
                         :profile-picture     #:keechma.controller {:params (fn [{:keys [router member-id]}]
                                                                              (and member-id
                                                                                   (or (:is-active (get (:routes router) [:home]))
                                                                                       (:is-active (get (:routes router) [:account-overview])))))
                                                                     :deps   [:router :jwt :member-id :image-upload]}
                         
                         :modal                #:keechma.controller {:params true}
                         
                         :be-error-handler        #:keechma.controller {:params true}}})