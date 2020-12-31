(ns app.main
  (:require [keechma.next.helix.core :refer [with-keechma dispatch use-sub]]
            ["react-native" :refer [View Text TouchableOpacity]]
            ["@react-navigation/native" :refer [NavigationContainer]]
            [helix.hooks :as hooks]
            [app.lib :refer [defnc $]]
            [app.ui.svgs :refer [Svg]]
            [app.tailwind :refer [tw get-color]]
            [app.rn.navigation :refer [create-stack-navigator screen navigator]]
            [app.ui.components.modal :refer [DefaultModal]]

            [app.ui.screens.onboarding :as onboarding]
            [app.ui.screens.home :as home]
            [app.ui.screens.providers.behavioral-health :as behavioral-health]
            [app.ui.screens.providers.dermatology :as dermatology]
            [app.ui.screens.providers.teledentistry :as teledentistry]
            [app.ui.screens.providers.telemedicine :as telemedicine]
            [app.ui.screens.providers.telespine :as telespine]

            [app.ui.screens.account-overview :as account-overview]
            [app.ui.screens.account.settings-and-security :as settings-and-security]
            [app.ui.screens.account.password-settings :as password-settings]
            [app.ui.screens.account.faqs :as faqs]
            [app.ui.screens.account.plan-information :as plan-information]
            [app.ui.screens.account.flexcare-id-card :as id-card]
            [app.ui.screens.account.legal :as legal]

            [app.ui.screens.person :as person]
            [app.ui.screens.project :as project]
            [app.ui.screens.role :as role]

            [app.ui.screens.register :as register]
            [app.ui.screens.forgot-password :as forget-password]
            [app.ui.screens.email-sent :as email-sent]
            [app.ui.screens.signin :as signin]))

(def main-stack (create-stack-navigator))

(def theme
  {:dark false
   :colors {:primary "white"
            :background (get-color :white)
            :card (get-color :white)
            :text (get-color :gray-dark)
            :border (get-color :gray-dark)}})

(def headerTitleStyle {:fontFamily "EBGaramond-ExtraBold"
                       :fontSize 22
                       :color "#000"})

(def headerStyle {:shadowColor "transparent"
                  :shadowRadius 0
                  :borderBottomWidth 0
                  :elevation 0
                  :shadowOpacity 0
                  :height 75})


(defn commonScreenOptions [{:keys [title]}]
  #js{:headerTitle (or title "")
      :headerTitleStyle (clj->js headerTitleStyle)
      :headerStyle (clj->js headerStyle)
      :headerTitleAlign "center"
      :headerTintColor "#5A5AF3"
      :headerBackTitleVisible false
      :headerLeftContainerStyle #js {:paddingLeft 8}
      :headerShown (some? title)})

(defnc HeaderWithInfo [{:keys [text modal] :as props}]
  ($ View {:style [{:flex 1} (tw :flex-row :items-center :justify-center)]}
     ($ Text {:style headerTitleStyle} text)
     ($ TouchableOpacity
        {:onPress #(dispatch props :modal :open modal)}
        ($ View {:style [(tw :w-5 :h-5)]}
           ($ Svg {:type :info})))))

(defnc Routes [props]
  ($ (navigator main-stack)       
         ($ (screen main-stack)
            {:name "onboarding"
             :component onboarding/Screen
             :options (commonScreenOptions {:title "Navigacijski panel"})})
         ($ (screen main-stack)
            {:name "person"
             :component person/Screen
             :options (commonScreenOptions {:title "Lista osoba"})})
         ($ (screen main-stack)
            {:name "project"
             :component project/Screen
             :options (commonScreenOptions {:title "Lista projakata"})})
         ($ (screen main-stack)
             {:name "role"
              :component role/Screen
              :options (commonScreenOptions {:title "Sifrarnik uloga"})})



         ($ (screen main-stack)
            {:name "register"
             :component register/Screen
             :options (commonScreenOptions {:title "Register"})})
         ($ (screen main-stack)
            {:name "signin"
             :component signin/Screen
             :options (commonScreenOptions {:title "Sign in"})})
         ($ (screen main-stack)
            {:name "forgot-password"
             :component forget-password/Screen
             :options (commonScreenOptions {:title ""})})
         ($ (screen main-stack)
            {:name "email-sent"
             :component email-sent/Screen
             :options (commonScreenOptions {:title ""})})
         ($ (screen main-stack)
            {:name "home"
             :component home/Screen
             :options (commonScreenOptions {})})
         ($ (screen main-stack)
            {:name "legal"
             :component legal/Screen
             :options (commonScreenOptions {:title "Legal"})})))

(defnc Main [props]
      ($ Routes {& props}))

(defonce router-state (atom nil))

(defnc RootRenderer [props]
  (let [navigation-ref* (hooks/use-ref nil)]
    ($ NavigationContainer
       {:onStateChange (fn [state]
                         (reset! router-state state)
                         (dispatch props :router :route-change state))
        :onReady #(dispatch props :router :register-navigator @navigation-ref*)
        :ref navigation-ref*
        :initialState @router-state
        :theme (clj->js theme)}
       ($ Main {& props})
       ($ DefaultModal))))

(def Root (with-keechma RootRenderer))