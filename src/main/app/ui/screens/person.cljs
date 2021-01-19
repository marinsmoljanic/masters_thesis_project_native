(ns app.ui.screens.person
  (:require [helix.hooks :as hooks]
            [app.lib :refer [$ defnc]]
            [app.tailwind :refer [tw]]
            [oops.core :refer [oget ocall]]
            [app.util :refer [resolve-error]]

            [app.ui.svgs :refer [Svg]]
            [app.ui.components.buttons :as buttons]
            [app.ui.components.inputs :refer [wrapped-input]]
            [app.ui.components.text :refer [Subtitle FormTitle]]
            [app.ui.components.screen-container :refer [ScreenContainer]]
            [app.ui.components.shared :refer [MarginV ButtonBigGray Errors]]

            [app.rn.animated :as a]
            [app.rn.animated :as animated]
            [app.rn.navigation :refer [navigate]]

            [keechma.next.controllers.form :as form]
            [keechma.next.helix.core :refer [with-keechma dispatch use-meta-sub use-sub]]

            ["expo-av" :refer [Video]]
            ["expo-linking" :as Linking]
            ["react-native-modal" :default Modal]
            ["lottie-react-native" :as LottieView]
            ["@react-navigation/native" :refer [useNavigation]]
            ["react-native-keyboard-aware-scroll-view" :refer [KeyboardAwareScrollView]]
            ["react-native" :refer [View Image Text TextInput ScrollView TouchableOpacity Animated]]))

(def AnimatedView (oget Animated :View))

(def duration 600)

(defnc RegisterPageContainer [{:keys [children]}]
       ($ View {:style [(tw :h-full :w-full :flex :flex-1 :px-4 :pb-10)]} children))

(defnc TableHeader [_]
       ($ View {:style [(tw :flex :flex-row :bg-purple :mt-4)]}
          ($ View {:style [(tw :flex :items-center :justify-center :pt-2 :pb-2 :border-r-2 :border-white :border-solid) {:width "30%"}]}
             ($ Text {:style [(tw :text-white :px-2 :text-center)
                              {:font-size 17
                               :line-height 22}]} "Ime"))
          ($ View {:style [(tw :flex :items-center :justify-center :pt-2 :pb-2 ) {:width "30%"}]}
             ($ Text {:style [(tw :text-white)
                              {:font-size 17
                               :line-height 22}]} "Prezime"))
          ($ View {:style [(tw :flex :items-center :justify-center :pt-2 :pb-2 :border-l-2 :border-white :border-solid) {:width "40%"}]}
             ($ Text {:style [(tw :text-white)
                              {:font-size 17
                               :line-height 22}]} "OIB"))))

(defnc ClickableRow [{:keys [name surname personalId id navigation]}]
      ($ TouchableOpacity
         {:onPress #(navigate navigation "person-edit" #js{:firstName name
                                                           :lastName  surname
                                                           :id        id})

          :activeOpacity 0.9
          :style (tw :flex :flex-row :bg-white :w-full :border-b :border-l :border-r :border-solid :border-gray-light)}

         ($ View {:style [(tw :flex :items-start :justify-center :py-2) {:width "30%"}]}
            ($ animated/Text
               {:style [(tw :text-black :px-2)
                        {:font-size 15}]}
               name))
         ($ View {:style [(tw :flex :items-start :justify-center :py-2) {:width "30%"}]}
            ($ animated/Text
               {:style [(tw :text-black :px-2)
                        {:font-size 15}]}
               surname))
         ($ View {:style [(tw :flex :items-start :justify-center :py-2) {:width "40%"}]}
            ($ animated/Text
               {:style [(tw :text-black :px-2)
                        {:font-size 15}]}
               personalId))
         ))

(defnc PersonButtonView [{:keys [navigation]}]
       ($ View {:style [(tw :mt-8)]}
          ($ buttons/Rounded {:onPress #(navigate navigation "person-add")
                              :title    "Dodaj novu osobu"
                              :text-style [(tw :text-white)]})))

(defnc ScreenRenderer [props]
       (let [navigation (useNavigation)
             persons    (use-sub props :persons)
             [visible set-visible] (hooks/use-state false)
             image-uri (:uri (use-sub props :image-upload))
             image-upload-top-value (hooks/use-ref (animated/value 500))
             subtitle-top-value (hooks/use-ref (animated/value 500))
             animate-image-upload (hooks/use-ref (animated/timing @image-upload-top-value {:duration duration :to-value 0}))
             animate-subtitle (hooks/use-ref (animated/timing @subtitle-top-value {:duration duration :to-value 0}))]

            (hooks/use-effect :once
                              (-> (ocall Animated :stagger 200
                                         #js[@animate-image-upload
                                             @animate-subtitle])
                                  (animated/start)))

            ($ ScreenContainer
               ($ ScrollView {:scrollIndicatorInsets #js{:right 1}}
                  ($ KeyboardAwareScrollView
                     {:style [(tw :flex :flex-1)]
                      :enableOnAndroid true
                      :extraScrollHeight 75
                      :extraHeight 75
                      :keyboardShouldPersistTaps "always"}
                     ($ RegisterPageContainer
                        ($ AnimatedView {:style [(tw :relative)
                                                 {:top @subtitle-top-value
                                                  :opacity (animated/interpolate @subtitle-top-value {:input-range [0 25 50] :output-range [1 0.5 0]})}]}
                           ($ TableHeader)
                           (map (fn [person]
                                    ($ ClickableRow {:name       (:FirstName  person)
                                                     :surname    (:LastName   person)
                                                     :personalId (:PersonalId person)
                                                     :id         (:id         person)
                                                     :key        (:id         person)
                                                     &           props}))
                                persons)

                           ($ PersonButtonView {& props}))))))))

(def Screen (with-keechma ScreenRenderer))
