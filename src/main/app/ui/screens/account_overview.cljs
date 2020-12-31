(ns app.ui.screens.account-overview
  (:require ["react-native" :refer [View Image Text TouchableOpacity Animated]]
            [keechma.next.helix.core :refer [with-keechma dispatch use-meta-sub use-sub]]
            [app.lib :refer [$ defnc]]
            [app.tailwind :refer [tw]]
            [helix.hooks :as hooks]
            [keechma.next.controllers.pipelines :refer [get-promise]]
            [app.rn.navigation :refer [navigate]]
            [app.ui.components.screen-container :refer [ScreenContainer]]
            [app.ui.components.buttons :as buttons]
            ["@react-navigation/native" :refer [useNavigation]]
            ["expo-av" :refer [Video]]
            [app.ui.components.loader :refer [Loader]]
            [app.ui.svgs :refer [Svg]]
            [app.ui.components.text :as text]
            [app.ui.components.inputs :refer [wrapped-input]]
            ["react-native-keyboard-aware-scroll-view" :refer [KeyboardAwareScrollView]]
            [app.ui.components.carousel :refer [Carousel]]
            [oops.core :refer [oget ocall]]
            [keechma.next.controllers.form :as form]
            [app.rn.animated :as animated]
            ["expo-constants" :default Constants]
            [app.ui.components.shared :refer [MarginV ButtonBigGray]]))

(def screens
  [{:title "Plan Information"
    :image :plan-information
    :screen "plan-information"}
   {:title "FlexCare ID Card"
    :image :id-card
    :screen "flexcare-id-card"}
   {:title "Settings & Security"
    :image :settings
    :screen "settings-and-security"}
   {:title "FAQs"
    :image :faq
    :screen "faqs"}
   {:title "Legal"
    :image :legal
    :screen "legal"}])

(defnc PageContainer [{:keys [children]}]
  ($ View {:style [(tw :flex :flex-1 :px-4 :pb-10)]} children))

(defnc ProfilePicture [props]
  (let [current-user (use-sub props :current-user)
        profile-picture (use-sub props :profile-picture)
        profile-picture-meta (use-meta-sub props :profile-picture)
        image-uri (or (:uri profile-picture) (:base-64 profile-picture))]
    ($ View {:style [(tw :flex :mb-10 :justify-center :items-center :mt-6 :relative)]}
       ($ TouchableOpacity {:onPress #(dispatch props :image-upload :permission)
                            :style [(tw :w-24 :h-24 :rounded-full :mb-4)]}
          (if (get-promise profile-picture-meta :load-picture)
            ($ Loader)
            (if image-uri
              (if (get-promise profile-picture-meta :upload-picture)
                ($ Loader)
                ($ Image {:source #js{:uri image-uri}
                          :style  {:width  "100%" :height "100%" :border-radius 50}}))
              ($ Svg {:type :profile-picture}))))
       
       ($ View
          ($ Text {:style [(tw :font-bold :text-lg :text-black)]}
             (str (get current-user :first_name) " " (get current-user :last_name))))
       
       ($ TouchableOpacity
          {:onPress #(dispatch props :image-upload :permission)}
          ($ Text {:style [(tw :underline :text-sm :text-gray)]}
             "Update Profile Picture")))))

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
(let [device (js->clj (oget Constants :platform) :keywordize-keys true)
      sys-version (str (or (get-in device [:ios :buildNumber])
                           (get-in device [:android :versionCode])))]
  ($ ScreenContainer
     ($ PageContainer
        ($ ProfilePicture {& props})
        ($ View {:style []}
           (map-indexed #($ Item {:key %1 :data %2 & props}) screens))
        ($ View {:style [(tw :mt-10)]}
           ($ TouchableOpacity
              {:onPress #(dispatch props :jwt :log-out)}
              ($ Text {:style [(tw :underline :text-black)]}
                 "Log out")))
        ($ Text {:style [(tw :text-sm :text-gray :pt-4)]} "Version:" sys-version)))))

(def Screen (with-keechma ScreenRenderer))