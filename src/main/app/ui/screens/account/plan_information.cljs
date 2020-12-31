(ns app.ui.screens.account.plan-information
  (:require ["react-native" :refer [View Image Text TouchableOpacity Animated]]
            [keechma.next.helix.core :refer [with-keechma dispatch use-meta-sub use-sub]]
            [keechma.next.controllers.pipelines :refer [get-promise]]
            [app.lib :refer [$ defnc]]
            [app.domain.providers :as providers]
            [app.tailwind :refer [tw]]
            [helix.hooks :as hooks]
            [app.rn.navigation :refer [navigate]]
            [app.ui.components.screen-container :refer [ScreenContainer]]
            [app.ui.components.buttons :as buttons]
            ["@react-navigation/native" :refer [useNavigation]]
            ["expo-av" :refer [Video]]
            [app.ui.components.loader :refer [Loader]]
            [app.ui.svgs :refer [Svg]]
            [app.ui.components.text :as text]
            ["expo-linking" :as Linking]
            [app.ui.components.inputs :refer [wrapped-input]]
            ["react-native-keyboard-aware-scroll-view" :refer [KeyboardAwareScrollView]]
            [app.ui.components.carousel :refer [Carousel]]
            [oops.core :refer [oget ocall]]
            [keechma.next.controllers.form :as form]
            [app.rn.animated :as animated]
            [app.ui.components.shared :refer [MarginV ButtonBigGray]]))

(defn prepare-data [providers data]
  (reduce (fn [acc item]
            (let [external-id (providers/get-provider-external-id item)
                  title (providers/get-provider-title item)
                  provider (first (filter #(= (:telemedproviderid %) external-id) data))
                  fee (case item
                        :telespine "00"
                        :behavioral-health nil
                        (:providerpackagefee provider))]
              (conj acc {:title title :price fee})))
          []
          providers))

(defnc PageContainer [{:keys [children]}]
  ($ View {:style [(tw :flex :flex-1 :px-4 :pb-10 :flex :items-center :justify-between :mt-10)]} children))

(defnc Item [{:keys [data]}]
  ($ View {:style [(tw :flex :flex-row :justify-center :items-center :my-3)]}
     ($ Text {:style [(tw :text-gray :text-lg :pr-3)]}
        (:title data))
     ($ Text {:style [{:font-family "EBGaramond-ExtraBold"}(tw :text-purple :text-lg)]}
        (if (:price data)
          (str "$" (:price data)) 
          "Varies"))))

(defnc Fee [props]
  (let [current-user (use-sub props :current-user)
        provider-ids (providers/get-complete-providers-access (:telemedproviders current-user))
        member-providers (use-sub props :member-providers)
        prepared-data (prepare-data provider-ids member-providers)]
    ($ View {:style [(tw :flex-auto)]}
       ($ Text {:style [{:font-size 22 :font-family "EBGaramond-ExtraBold"}(tw :text-black :font-bold)]}
          "Consultation Fee per Visit")
       ($ View {:style [(tw :rounded-18 :border-2 :border-gray-light-2 :py-3 :px-4 :mt-5)]}
          (map-indexed #($ Item {:key %1 :data %2}) prepared-data)))))

(defnc Group [props]
  (let [group (use-sub props :member-group)
        group-id (get group :eligibilitygroup)
        current-user (use-sub props :current-user)
        member-number (:membernumber current-user)]
    ($ View {:style (tw :flex-auto)}
       ($ Text {:style [{:font-size 22 :font-family "EBGaramond-ExtraBold"}(tw :text-black :font-bold :text-center)]}
          "Group ID / Member ID")
       ($ View {:style [(tw :rounded-18 :border-2 :border-gray-light-2 :mt-5)]}
          ($ Text {:style [(tw :text-purple :text-lg :px-4 :py-3 :text-center)]}
             (str group-id " / " member-number))))))

(defnc ScreenRenderer [props]
  (let [providers-meta (use-meta-sub props :member-providers)
        group-meta (use-meta-sub props :member-group)]
    (if (or
         (get-promise providers-meta :load-member-providers)
         (get-promise providers-meta :load-member-group))
      ($ Loader)
      ($ ScreenContainer {:style (tw :pb-4)}
         ($ PageContainer
            ($ View
               ($ Group {& props})
               ($ Fee {& props}))
            ($ View
               ($ Text {:style [(tw :text-center)]}
                  ($ Text {:style [(tw :text-gray :text-base)]}
                     "For technical support, please email ")
                  ($ TouchableOpacity {:onPress #(ocall Linking :openURL "mailto:support@FlexCare.com")}
                     ($ Text {:style [(tw :text-purple :text-base)]}
                        "support@FlexCare.com")))))))))

(def Screen (with-keechma ScreenRenderer))
