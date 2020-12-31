(ns app.controllers.forms.register
  (:require [keechma.next.controller :as ctrl]
            [keechma.next.controllers.pipelines :as pipelines]
            [keechma.pipelines.core :as pp :refer-macros [pipeline!]]
            [keechma.next.controllers.form :as form]
            [keechma.next.controllers.router :as router]
            [app.validators :as v]
            [app.rn.navigation :refer [navigate]]
            [app.settings :refer [api-key]]
            ["@react-navigation/native" :refer [useNavigation]]
            [app.api :as api]))

(derive :register-form ::pipelines/controller)

(defn prepare-data [data]
  {:apikey api-key
   :firstname (:first-name data)
   :lastname (:last-name data)
   :membernumber (:membership data)
   :emailaddress (:email data)
   :password (:password data)})



(def pipelines
  {:keechma.form/submit-data
   (pipeline! [value {:keys [meta-state* deps-state*] :as ctrl}]
              (let [uri (:uri (:image-upload @deps-state*))
                    ext (second (clojure.string/split uri #"\."))
                    base-64 (or (:base-64 (:image-upload @deps-state*)) "")
                    data (if uri
                           (merge (prepare-data value) {:photo     base-64
                                                        :photo_ext ext})
                           (prepare-data value))]
                (pipeline! [value {:keys [state*] :as ctrl}]
                           (api/register data)
                           (if (seq (select-keys value [:issues]))
                             (throw (ex-info "Error " value))
                             (ctrl/dispatch ctrl :modal :open {:type :register
                                                               :cb #(ctrl/dispatch ctrl :router :redirect "signin")}))

                           (rescue! [error]
                                   (pp/reset! state* {:error (ex-data error)})))))})

(defmethod ctrl/prep :register-form [ctrl]
  (pipelines/register ctrl
                      (form/wrap pipelines
                                 (v/to-validator {:membership [:not-empty :numeric]
                                                  :terms-and-conditions [:terms-and-conditions-checked?]
                                                  :first-name [:not-empty]
                                                  :last-name [:not-empty]
                                                  :email    [:email :not-empty]
                                                  :password [:not-empty :password?]}))))