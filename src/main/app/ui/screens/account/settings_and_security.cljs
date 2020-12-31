(ns app.ui.screens.account.settings-and-security
  (:require ["react-native" :refer [View Image Text TouchableOpacity Animated]]
            [keechma.next.helix.core :refer [with-keechma dispatch use-meta-sub use-sub]]
            [app.lib :refer [$ defnc]]
            [app.tailwind :refer [tw]]
            [helix.hooks :as hooks]
            [app.rn.navigation :refer [navigate]]
            [app.ui.components.screen-container :refer [ScreenContainer]]
            [app.ui.components.buttons :as buttons]
            ["@react-navigation/native" :refer [useNavigation]]
            ["expo-av" :refer [Video]]
            [app.ui.svgs :refer [Svg]]
            [app.ui.components.text :as text]
            [app.ui.components.inputs :refer [wrapped-input]]
            ["react-native-keyboard-aware-scroll-view" :refer [KeyboardAwareScrollView]]
            [app.ui.components.carousel :refer [Carousel]]
            [oops.core :refer [oget ocall]]
            [keechma.next.controllers.form :as form]
            [app.rn.animated :as animated]
            [app.ui.components.shared :refer [MarginV ButtonBigGray]]))

(def screens
  [{:title "Password Settings"
    :image :password
    :screen "password-settings"}])

(defnc PageContainer [{:keys [children]}]
  ($ View {:style [(tw :flex :flex-1 :px-4 :pb-10)]} children))

(defnc Item [{:keys [data] :as props}]
  (let [navigation (useNavigation)
        image-uri (:image data)
        title (:title data)
        screen (:screen data)]
    ($ TouchableOpacity
       {:style [(tw :flex :flex-row :w-full :mt-2 :pb-3 :justify-between :border-b-2 :border-gray-lighter)]
        :onPress #(navigate navigation screen)}
       ($ View {:style [(tw :flex :flex-row :justify-center :items-center)]}
          ($ View {:style [(tw :w-8 :h-8 :mr-3)]}
             ($ Svg {:type image-uri}))
          ($ Text {:style [(tw :text-lg)
                           {:font-weight "600"}]} title))
       ($ View {:style [(tw :w-8 :h-8 :mr-3)]}
          ($ Svg {:type :arrow-right})))))

(defnc ScreenRenderer [props]
  ($ ScreenContainer
     ($ PageContainer
        ($ View {:style [(tw :mt-10)]}
           (map-indexed #($ Item {:key %1 :data %2 & props}) screens)))))

(def Screen (with-keechma ScreenRenderer))
