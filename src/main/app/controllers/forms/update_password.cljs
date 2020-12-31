(ns app.controllers.forms.update-password
  (:require [keechma.next.controller :as ctrl]
            [keechma.next.controllers.pipelines :as pipelines]
            [keechma.pipelines.core :as pp :refer-macros [pipeline!]]
            [keechma.next.controllers.form :as form]
            [keechma.next.controllers.router :as router]
            [app.validators :as v]
            [app.settings :refer [api-key]]
            [app.api :as api]))

(derive :update-password-form ::pipelines/controller)

(defn prepare-data [data]
  {:apikey api-key
   :originalpwd (:old-password data)
   :updatedpwd (:password data)
   :memberrecid (:memberrecid data)})

(def pipelines
  {:keechma.form/submit-data
   (pipeline! [value {:keys [meta-state* deps-state*] :as ctrl}]
              (let [member-id (get-in @deps-state* [:current-user :member_record_id])
                    jwt (get @deps-state* :jwt)
                    data (prepare-data (merge {:memberrecid member-id} value))]
                (pipeline! [value {:keys [state*] :as ctrl}]
                           (api/update-password {:data data :jwt jwt})
                           (if (seq (select-keys value [:issues]))
                             (throw (ex-info "Error" value))
                             (ctrl/dispatch ctrl :router :redirect "account-overview"))
                           
                           (rescue! [error]
                                    (let [expired-token (= "Expired token" (get-in (ex-data error) [:value :response :original :issues :issuemessage]))]
                                      (if expired-token
                                        (do
                                          (ctrl/dispatch ctrl :jwt :log-out)
                                          (ctrl/dispatch ctrl :expired-token-handler :throw-error))
                                        (pp/reset! state* {:error (ex-data error)})))))))})

(defmethod ctrl/prep :update-password-form [ctrl]
  (pipelines/register ctrl
                      (form/wrap pipelines
                                 (v/to-validator {:old-password [:not-empty]
                                                  :password [:password? :not-empty]
                                                  :password2 [:password-confirmation :not-empty]}))))
