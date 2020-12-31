(ns app.domain.providers
  (:require [secretary.core :as s]
            [app.settings :refer [base-url api-key]]))

(def providers
  [{:id          :telemedicine
    :external-id 2
    :title       "TeleMedicine"
    :description "For your general health."
    :bg-color    :bg-blue
    :icon        (js/require "../assets/HomeScreen/TeleMed.png")
    :lottie      (js/require "../assets/lottie/TeleMed.json")
    :screen      "telemedicine"}

   {:id          :behavioral-health
    :external-id 2
    :title       "Behavioral Health"
    :description "For your mind."
    :bg-color    :bg-yellow-2
    :icon        (js/require "../assets/HomeScreen/BehavioralHealth.png")
    :lottie      (js/require "../assets/lottie/BehavioralHealth.json")
    :screen      "behavioral-health"}

   #_{:id          :dermatology
    :external-id 2
    :title       "Dermatology"
    :description "For your skin."
    :bg-color    :bg-gray-light
    :icon        (js/require "../assets/HomeScreen/Dermatology.png")
    :lottie      (js/require "../assets/lottie/Dermatology.json")
    :screen      "dermatology"}

   {:id          :telespine
    :external-id 11
    :title       "TeleSpine"
    :description "For your posture."
    :bg-color    :bg-purple-2
    :icon        (js/require "../assets/HomeScreen/TeleSpine.png")
    :lottie      (js/require "../assets/lottie/TeleSpine.json")
    :screen      "telespine"}

   {:id          :teledentistry
    :external-id 13
    :title       "TeleDentistry"
    :description "For your smile."
    :bg-color    :bg-red-2
    :icon        (js/require "../assets/HomeScreen/TeleDentistry.png")
    :lottie      (js/require "../assets/lottie/TeleDentistry.json")
    :screen      "teledentistry"}
   
   {:id          :teladoc
    :external-id 1
    :title       "TelaDoc"
    :description "For your general health."
    :bg-color    :bg-blue
    ;:icon        (js/require "../assets/HomeScreen/TeleDentistry.png")
    ;:lottie      (js/require "../assets/lottie/TeleDentistry.json")
    :screen      "teladoc"}])

(defn get-provider-external-id [local-provider-id]
  (get
   (first (filter #(= (:id %) local-provider-id) providers))
   :external-id))

(defn get-provider-title [local-provider-id]
  (->> providers
       (filter #(= (:id %) local-provider-id))
       first
       :title))

(defn get-single-provider-access [provider]
  (let [value (:value provider)
        package (:providerpackage provider)]
    (cond
      (= value "Teledentists") [:teledentistry]
      (= value "Telespine") [:telespine]
      (= value "Teladoc") [:teladoc]
      (contains? #{"0TO" "20TO" "45TO"} package) [:telemedicine]
      (contains? #{"0TBH" "20TBH" "45TBH"} package) [:telemedicine :behavioral-health]
      (contains? #{"0TD" "20TD" "45TD"} package) [:telemedicine #_(:dermatology)]
      (contains? #{"0TBHD" "20TBHD" "45TBHD"} package) [:telemedicine :behavioral-health #_(:dermatology)]
      (contains? #{"0BH" "0BHLC"} package) [:behavioral-health]
      :else [])))

(defn get-complete-providers-access [providers]
  (vec (set (flatten (map get-single-provider-access providers)))))

(defn get-providers [provider-ids]
  (filter (fn [p] (contains? (set provider-ids) (:id p))) providers))

(def transfer-user-endpoint
  (str base-url "/TransferUser"))

(defn get-external-url [local-provider-id token]
  (str transfer-user-endpoint "?" (s/encode-query-params {:token token
                                                          :provider (get-provider-external-id local-provider-id)
                                                          :apikey api-key})))
