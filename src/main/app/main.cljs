(ns app.main
  (:require [keechma.next.helix.core :refer [with-keechma dispatch use-sub]]
            ["react-native" :refer [View Text TouchableOpacity]]
            ["@react-navigation/native" :refer [NavigationContainer]]
            [helix.hooks :as hooks]
            [app.lib :refer [defnc $]]
            [app.ui.svgs :refer [Svg]]
            [app.tailwind :refer [tw get-color]]
            [app.rn.navigation :refer [create-stack-navigator screen navigator]]

            [app.ui.screens.onboarding :as onboarding]
            [app.ui.screens.person :as person]
            [app.ui.screens.person-add :as person-add]
            [app.ui.screens.person-edit :as person-edit]

            [app.ui.screens.project :as project]
            [app.ui.screens.project-add :as project-add]
            [app.ui.screens.project-edit :as project-edit]

            [app.ui.screens.role :as role]

            [app.ui.screens.person-role :as person-role]
            [app.ui.screens.person-role-by-person :as person-role-by-person]
            [app.ui.screens.person-role-by-project :as person-role-by-project]))

(def main-stack (create-stack-navigator))

(def theme
  {:dark false
   :colors {:primary "white"
            :background (get-color :white)
            :card (get-color :white)
            :text (get-color :gray-dark)
            :border (get-color :gray-dark)}})

(def headerTitleStyle {:fontSize 22
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
             :options (commonScreenOptions {:title "Lista projekata"})})
         ($ (screen main-stack)
            {:name "project-add"
             :component project-add/Screen
             :options (commonScreenOptions {:title "Dodaj novi projekt"})})
         ($ (screen main-stack)
            {:name "project-edit"
             :component project-edit/Screen
             :options (commonScreenOptions {:title "Uredi podatke projekta"})})
         ($ (screen main-stack)
             {:name "role"
              :component role/Screen
              :options (commonScreenOptions {:title "Šifrarnik uloga"})})
         ($ (screen main-stack)
            {:name "person-add"
             :component person-add/Screen
             :options (commonScreenOptions {:title "Dodaj novu osobu"})})
         ($ (screen main-stack)
            {:name "person-edit"
             :component person-edit/Screen
             :options (commonScreenOptions {:title "Uredi podatke osobe"})})
         ($ (screen main-stack)
            {:name "person-role"
             :component person-role/Screen
             :options (commonScreenOptions {:title "Dodaj novo zaduženje"})})
         ($ (screen main-stack)
            {:name "person-role-by-person"
             :component person-role-by-person/Screen
             :options (commonScreenOptions {:title "Uredi zaduženje osobe"})})
         ($ (screen main-stack)
            {:name "person-role-by-project"
             :component person-role-by-project/Screen
             :options (commonScreenOptions {:title "Uredi zaduženje projektaaa"})})))

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
       ($ Routes {& props}))))

(def Root (with-keechma RootRenderer))