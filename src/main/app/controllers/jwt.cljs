(ns app.controllers.jwt
  (:require [keechma.next.controller :as ctrl]
            [keechma.next.controllers.pipelines :as pipelines]
            [keechma.pipelines.core :as pp :refer-macros [pipeline!]]
            [keechma.next.controllers.form :as form]
            [oops.core :refer [ocall oget oset!]]
            [keechma.next.controllers.router :as router]
            [app.validators :as v]
            [app.api :as api]
            [app.settings :refer [api-key]]
            [promesa.core :as p]
            ["@react-native-async-storage/async-storage" :default AsyncStorage]))

(derive :jwt ::pipelines/controller)

(defn get-jwt []
  (->> (ocall AsyncStorage :getItem "@jwt")
       (p/map (fn [res]
                (if res
                  (->> (api/verify-jwt {:jwt  res
                                        :data {:apikey api-key}})
                       (p/map (fn [verify-res]
                                (if (:error verify-res)
                                  (throw (ex-info "invalid-jwt" verify-res))
                                  res))))
                  (throw (ex-info "invalid-jwt" {})))))))

(def start (-> (pipeline! [value {:keys [state*]
                                  :as   ctrl}]
                          (get-jwt)
                          (pp/reset! state* value)
                          (rescue! [error]
                                   (ocall AsyncStorage :removeItem "@jwt")
                                   (pp/reset! state* nil)
                                   (ctrl/dispatch ctrl :member-id :set-member-id nil)))
            (pp/set-queue :load-jwt)))

(def pipelines
  {:set-jwt
   (pipeline! [value {:keys [state*] :as ctrl}]
              (ocall AsyncStorage :setItem "@jwt" (:jwt value))
              (pp/reset! state* (:jwt value))
              (ctrl/dispatch ctrl :member-id :set-member-id (str (:memberrecid value)))
              (rescue! [error]
                       (pp/reset! state* nil)))

   :keechma.on/start start

   :app-state/change (pipeline! [value {:keys [meta-state*]}]
                                (if (= :active value)
                                  (let [inactive-at (::inactive-at @meta-state*)
                                        should-logout (<= (* 1000 60 60 2) (- (ocall js/Date :now) inactive-at))]
                                    (pipeline! [value {:keys [meta-state*] :as ctrl}]
                                               (pp/swap! meta-state* dissoc ::inactive-at)
                                               (when should-logout
                                                 (ctrl/dispatch ctrl (:keechma.controller/name ctrl) :log-out))))
                                  (pp/swap! meta-state* assoc ::inactive-at (ocall js/Date :now))))
   
   :log-out (pipeline! [value {:keys [state*] :as ctrl}]
                       (ocall AsyncStorage :removeItem "@jwt")
                       (reset! state* nil)
                       (ctrl/dispatch ctrl :member-id :set-member-id nil)
                       (ctrl/dispatch ctrl :router :reset (clj->js {:index  1
                                                                    :routes [{:name   "signin"
                                                                              :params nil}]}))
                       (rescue! [error]
                                (pp/reset! state* nil)))})

(defmethod ctrl/prep :jwt [ctrl]
  (-> ctrl
      (pipelines/register pipelines)))

(defmethod ctrl/derive-state :jwt [_ state _]
  state)