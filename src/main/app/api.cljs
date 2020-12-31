(ns app.api
  (:require [keechma.next.toolbox.ajax :refer [GET POST DELETE PUT]]
            [app.settings :refer [api-url]]
            [ajax.core :as ajax]
            [ajax.protocols :as protocols]
            [promesa.core :as p]
            [camel-snake-kebab.core :refer [->kebab-case-keyword]]))

(defn pdf-error-handler [error]
  {:error error})

(def default-request-config
  {:response-format :json
   :keywords? true
   :format :json})

(def pdf-config
  {:response-format {:content-type "application/pdf" :description "MEMBER ID CARD - PDF" :read protocols/-body :type :blob}})

(defn add-auth-header [req-params jwt]
  (if jwt
    (assoc-in req-params [:headers :authorization] (str "Bearer " jwt))
    req-params))

(defn add-params [req-params params]
  (if params
    (assoc req-params :params params)
    req-params))

(defn req-params [& {:keys [jwt data]}]
  (-> default-request-config
      (add-auth-header jwt)
      (add-params data)))

(defn req-params-pdf [& {:keys [jwt data]}]
  (-> pdf-config
      (add-auth-header jwt)
      (add-params data)))

(defn process-user [data]
  data)

(defn register [user]
  (->> (POST (str api-url "/Register")
             (req-params :data user))
       (p/map process-user)))

(defn signin [user]
  (->> (POST (str api-url "/Login")
             (req-params :data user))
       (p/map process-user)))

(defn verify-jwt [{:keys [jwt data]}]
  (->>
   (->
    (POST (str api-url "/VerifyJWT")
      (req-params :jwt jwt :data data))
    (p/catch (fn [e]
               {:error true})))
   (p/map process-user)))

(defn process-data [data]
  data)

(defn process-pdf [data]
  data)

(defn forgot-password [data]
  (->> (POST (str api-url "/ForgotPassword")
             (req-params :data data))
       (p/map process-data)))

(defn update-password [{:keys [jwt data]}]
  (->> (POST (str api-url "/UpdatePassword")
         (req-params :data data :jwt jwt))
       (p/map process-data)))

(defn update-member-profile-picture [{:keys [jwt data]}]
  (->> (POST (str api-url "/UpdateMemberProfilePicture")
         (req-params :data data :jwt jwt))
       (p/map process-data)))

(defn get-current-user [{:keys [jwt data]}]
  (->> (GET (str api-url "/GetMemberInfo")
         (req-params :data data :jwt jwt))
       (p/map process-data)))

(defn get-member-id-card [{:keys [jwt data]}]
  (p/catch
   (->> (GET (str api-url "/GetMemberIDCard")
          (req-params-pdf :data data :jwt jwt))
        (p/map process-pdf))
   pdf-error-handler))

(defn get-member-providers [{:keys [jwt data]}]
  (->> (GET (str api-url "/GetMemberProviders")
         (req-params :data data :jwt jwt))
       (p/map process-data)))

(defn get-member-group [{:keys [jwt data]}]
  (->> (GET (str api-url "/GetMemberGroupInfo")
         (req-params :data data :jwt jwt))
       (p/map process-data)))

(defn get-member-profile-picture [{:keys [jwt data]}]
  (->> (GET (str api-url "/GetMemberProfilePicture")
         (req-params :data data :jwt jwt))
       (p/map process-data)))