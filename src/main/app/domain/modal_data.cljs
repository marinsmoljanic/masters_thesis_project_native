(ns app.domain.modal-data
  (:require [app.ui.components.text :refer [Subtitle]]
            [oops.core :refer [ocall]]
            ["react-native" :refer [Text TouchableOpacity]]
            [app.lib :refer [$ defnc]]
            ["expo-linking" :as Linking]
            [app.tailwind :refer [tw]]))

(defnc ModalTitle [{:keys [children]}]
  ($ Text {:style [(tw :text-center)
                   {:font-size 20
                    :line-height 24
                    :font-weight "bold"}]}
     children))

(defnc ModalBody [{:keys [children]}]
  ($ Text {:style [(tw :text-center)]}
     children))

(defnc ModalBodyProvider [{:keys [text phone]}]
  ($ Subtitle {:style [(tw :text-center)]}
     text
     ($ Text {:style [(tw :flex :justify-end :items-end)
                                  {:height 24}]
                          :onPress #(ocall Linking :openURL (str "tel://" phone))}
        ($ Text {:style [(tw :text-purple)]}
           (str phone  " .")))))

(def data
  {:expired-jwt {:title ($ ModalTitle "Error")
                 :text ($ ModalBody "Session timed out. Please sign in again.")}
   
   :error       {:title ($ ModalTitle "Error")
                 :text ($ ModalBody "We are sorry. Something went wrong. Please try again.")}
   
   :register    {:title ($ ModalTitle "Congratulations")
                 :text ($ ModalBody "You have successfully connected to your FlexCare account.")}
   
   :telemedicine {:title ($ ModalTitle "TeleMedicine")
                 :text ($ ModalBodyProvider {:text "For support or to request a consult, please call "
                                              :phone "1-888-501-2405"})}
   
   :behavioral-health {:title ($ ModalTitle "Behavioral Health")
                       :text ($ ModalBodyProvider {:text "For support or to request a consult, please call "
                                                   :phone "1-888-501-2405"})}
   
   :dermatology      {:title ($ ModalTitle "Dermatology")
                      :text ($ ModalBodyProvider {:text "For support or to request a consult, please call "
                                             :phone "1-888-501-2405"})}
   
   :teledentistry    {:title ($ ModalTitle "TeleDentistry")
                      :text ($ ModalBodyProvider {:text "For technical support, please call "
                                               :phone "1-888-641-5505"})}
   
   :telespine        {:title ($ ModalTitle "TeleSpine")
                      :text ($ ModalBodyProvider {:text "For technical support, please call "
                                           :phone "1-800-647-3961"})}
   
   :default          {:title ($ ModalTitle "Error")
                      :text ($ ModalBody "We are sorry. Something went wrong. Please try again.")}})

(defn get-modal-data [modal-type]
  (case modal-type
    :expired-jwt (:expired-jwt data)
    :error (:error data)
    :register (:register data)
    :behavioral-health (:behavioral-health data)
    :dermatology (:dermatology data)
    :teledentistry (:teledentistry data)
    :telemedicine (:telemedicine data)
    :telespine (:telespine data)
    (:default data)))