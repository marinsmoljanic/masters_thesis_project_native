(ns app.ui.screens.register
  (:require ["react-native" :refer [View Image Text TextInput ScrollView TouchableOpacity Animated]]
            ["react-native-keyboard-aware-scroll-view" :refer [KeyboardAwareScrollView]]
            ["lottie-react-native" :as LottieView]
            [app.lib :refer [$ defnc]]
            [helix.hooks :as hooks]
            [app.ui.components.buttons :as buttons]
            [app.tailwind :refer [tw]]
            [app.ui.components.inputs :refer [wrapped-input]]
            [keechma.next.helix.core :refer [with-keechma dispatch use-meta-sub use-sub]]
            [app.ui.components.screen-container :refer [ScreenContainer]]
            [app.ui.components.text :refer [Subtitle FormTitle]]
            [keechma.next.controllers.form :as form]
            ["react-native-modal" :default Modal]
            [app.rn.animated :as animated]
            [app.rn.navigation :refer [navigate]]
            [oops.core :refer [oget ocall]]
            ["@react-navigation/native" :refer [useNavigation]]
            [app.ui.svgs :refer [Svg]]
            ["expo-av" :refer [Video]]
            ["expo-linking" :as Linking]
            [app.util :refer [resolve-error]]
            [app.ui.components.shared :refer [MarginV ButtonBigGray Errors]]))

(def AnimatedView (oget Animated :View))

(def duration 600)

(defnc RegisterPageContainer [{:keys [children]}]
  ($ View {:style [(tw :h-full :w-full :flex :flex-1 :px-4 :pb-10)]} children))

(defnc RegisterForm [props]
  (let [meta-state (use-meta-sub props :register-form)
        backend-errors (:error (use-sub props :register-form))
        checked? (form/get-data-in meta-state :terms-and-conditions)
        no-errors? (and (true? (form/valid? meta-state)) checked?)
        membership-top-value (hooks/use-ref (animated/value 100))
        first-name-top-value (hooks/use-ref (animated/value 100))
        last-name-top-value (hooks/use-ref (animated/value 100))
        title-top-value (hooks/use-ref (animated/value 100))
        email-top-value (hooks/use-ref (animated/value 100))
        password-top-value (hooks/use-ref (animated/value 100))
        terms-top-value (hooks/use-ref (animated/value 100))
        submit-top-value (hooks/use-ref (animated/value 100))
        animate-membership (hooks/use-ref (animated/timing @membership-top-value {:duration duration :to-value 0}))
        animate-first-name (hooks/use-ref (animated/timing @first-name-top-value {:duration duration :to-value 0}))
        animate-last-name (hooks/use-ref (animated/timing @last-name-top-value {:duration duration :to-value 0}))
        animate-title (hooks/use-ref (animated/timing @title-top-value {:duration duration :to-value 0}))
        animate-email (hooks/use-ref (animated/timing @email-top-value {:duration duration :to-value 0}))
        animate-password (hooks/use-ref (animated/timing @password-top-value {:duration duration :to-value 0}))
        animate-terms (hooks/use-ref (animated/timing @terms-top-value {:duration duration :to-value 0}))
        animate-submit (hooks/use-ref (animated/timing @submit-top-value {:duration duration :to-value 0}))]
    (hooks/use-effect :once
                      (->
                       (ocall Animated :sequence
                              #js[(ocall Animated :delay 600)
                                  (ocall Animated :stagger 100
                                         #js[@animate-membership
                                             @animate-first-name
                                             @animate-last-name
                                             @animate-title
                                             @animate-email
                                             @animate-password
                                             @animate-terms
                                             @animate-submit])])
                       (animated/start)))

    ($ View {:style [(tw :flex :flex-1)]}
       ($ View {:style [(tw :flex :flex-1)]}
          (wrapped-input {:keechma.form/controller :register-form
                          :input/type              :text
                          :input/attr              :membership
                          :placeholder             "FlexCare Membership ID"
                          :animation/top                     @membership-top-value
                          :animation/opacity (animated/interpolate @membership-top-value {:input-range [0 50 100] :output-range [1 0.5 0]})})
          (wrapped-input {:keechma.form/controller :register-form
                          :input/type              :text
                          :input/attr              :first-name
                          :placeholder             "First Name"
                          :animation/top                     @first-name-top-value
                          :animation/opacity (animated/interpolate @first-name-top-value {:input-range [0 50 100] :output-range [1 0.5 0]})})
          (wrapped-input {:keechma.form/controller :register-form
                          :input/type              :text
                          :input/attr              :last-name
                          :placeholder             "Last Name"
                          :animation/top                     @last-name-top-value
                          :animation/opacity (animated/interpolate @last-name-top-value {:input-range [0 50 100] :output-range [1 0.5 0]})})

          ($ MarginV {:spacing 15})

          ($ View {:style [(tw :flex :flex-1)]}
             ($ FormTitle {:text-style [(tw :text-center :text-black :pb-2)]
                           :animation-style {:top @title-top-value
                                             :opacity (animated/interpolate @title-top-value {:input-range [0 50 100] :output-range [1 0.5 0]})}
                           :text  "Create sign in credentials."})
             (wrapped-input {:keechma.form/controller :register-form
                             :input/type              :text
                             :input/attr              :email
                             :placeholder             "Email"
                             :keyboardType            "email-address"
                             :autoCapitalize          "none"
                             :animation/top                     @email-top-value
                             :animation/opacity (animated/interpolate @email-top-value {:input-range [0 50 100] :output-range [1 0.5 0]})})
             (wrapped-input {:keechma.form/controller :register-form
                             :input/type              :password
                             :input/attr              :password
                             :placeholder             "Password"
                             :animation/top                     @password-top-value
                             :animation/opacity (animated/interpolate @password-top-value {:input-range [0 50 100] :output-range [1 0.5 0]})}))

          ($ AnimatedView {:style [(tw :flex :flex-1)
                                   {:top @terms-top-value}]}
             (wrapped-input {:keechma.form/controller :register-form
                             :input/type              :checkbox
                             :input/attr              :terms-and-conditions
                             :animation/opacity (animated/interpolate @terms-top-value {:input-range [0 50 100] :output-range [1 0.5 0]})}))

          (when backend-errors
            ($ Errors {:errors backend-errors}))

          ($ AnimatedView {:style [(tw :w-full :flex :items-center :justify-center :mt-8)
                                   {:top @submit-top-value
                                    :opacity (animated/interpolate @submit-top-value {:input-range [0 50 100] :output-range [1 0.5 0]})}]}
             ($ View {:style [(tw "w-8/12")]}
                ($ buttons/Big {:onPress #(dispatch props :register-form :keechma.form/submit)
                                  :title    "Register"
                                  :style    (if no-errors? [(tw :bg-purple)] [(tw :bg-gray-light-2)])
                                  :text-style (if no-errors? [(tw :text-white)] [(tw :text-gray)])})))))))

(defnc ModalContent [{:keys [close]}]
  ($ View {:style [(tw :bg-white :rounded-18 :py-10 :px-4)]}
     ($ TouchableOpacity
        {:style [(tw :mr-4 :mt-4 :w-6 :h-6)
                 {:position "absolute"
                  :right 0
                  :top 0}]
         :onPress #(close)}
        ($ Svg {:type :close}))

     ($ View {:style [(tw :flex :justify-center :items-center)]}
        ($ LottieView
           {:source (js/require "../assets/lottie/Register.json")
            :autoPlay true
            :resizeMode "contain"
            :style {:width 200 :height 200}}))

     ($ View {:style [(tw :w-full :px-8 :mb-5 :flex :justify-center)]}
         ($ FormTitle {:text-style [(tw :text-center :text-black)]
                       :text  "Help us find your FlexCare account."}))

     ($ View
        ($ Text {:style [(tw :mb-4 :text-center :text-gray)
                         {:font-size 17
                          :line-height 22}]}
           "Connect to your FlexCare account. Type your Membership ID and name as it appears on your FlexCare ID card."))
     ($ Text {:style [(tw :text-center :text-gray)
                      {:font-size 17
                       :line-height 22}]}
        "If you cannot locate your FlexCare Membership ID card, please contact your Human Resources department or email "


        ($ Text {:style [(tw :text-purple)]
                 :onPress #(ocall Linking :openURL "mailto:support@FlexCare.com")}
           "support@FlexCare.com")
        ($ Text " for assistance."))))



(defnc SignInLink [props]
  (let [navigation (useNavigation)
        bottom-text-top-value (hooks/use-ref (animated/value 100))
        animate-bottom-text (hooks/use-ref (animated/timing @bottom-text-top-value {:duration duration :to-value 0}))]
    (hooks/use-effect :once
                      (->
                       (ocall Animated :sequence
                              #js[(ocall Animated :delay 1450)
                                  (ocall Animated :stagger 100
                                         #js[@animate-bottom-text])])
                       (animated/start)))
    ($ AnimatedView {:style [(tw :flex :flex-1)
                             {:top @bottom-text-top-value
                              :opacity (animated/interpolate @bottom-text-top-value {:input-range [0 50 100] :output-range [1 0.5 0]})}]}
       ($ View {:style [(tw :text-gray :text-center :text-sm :flex :flex-row :justify-center :flex-1 :text-center :mt-10)]}
          ($ TouchableOpacity
             ($ Text {:style [(tw :text-gray)]} "Already registered? "))
          ($ TouchableOpacity
             {:onPress #(navigate navigation "signin")}
             ($ Text {:style [(tw :underline :text-gray)]} "Sign In"))))))

(defnc ScreenRenderer [props]
  (let [navigation (useNavigation)
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
       ($ Modal {:isVisible visible
                 :onSwipeComplete #(set-visible false)
                 :swipeDirection "up"}
          ($ ModalContent {:close #(set-visible false)}))
       ($ KeyboardAwareScrollView
          {:style [(tw :flex :flex-1)]
           :enableOnAndroid true
           :extraScrollHeight 75
           :extraHeight 75
           :keyboardShouldPersistTaps "always"}
          ($ RegisterPageContainer
             ($ AnimatedView {:style [(tw :flex :flex-1 :justify-center :items-center :mt-6 :relative)
                                      {:top @image-upload-top-value
                                       :opacity (animated/interpolate @image-upload-top-value {:input-range [0 50 100] :output-range [1 0.5 0]})}]}

                ($ TouchableOpacity {:onPress #(dispatch props :image-upload :permission)
                                     :style [(tw :w-24 :h-24 :rounded-full :mb-4)]}
                   (if image-uri
                     ($ Image {:source #js{:uri image-uri}
                               :style  {:width  "100%" :height "100%" :border-radius 50}})
                     ($ Svg {:type :profile-picture})))

                ($ TouchableOpacity
                   {:onPress #(dispatch props :image-upload :permission)}
                   ($ Text {:style [(tw :underline :text-sm :text-gray)]}
                      "Add Profile Picture")))

             ($ AnimatedView {:style [(tw :relative)
                                      {:top @subtitle-top-value
                                       :opacity (animated/interpolate @subtitle-top-value {:input-range [0 25 50] :output-range [1 0.5 0]})}]}
                ($ View {:style [(tw :flex :items-center :justify-center :mt-5 :mb-6)]}
                   ($ Text {:style [(tw :text-center :text-gray :px-2)
                                    { :font-size 17
                                     :line-height 22}]} "Connect to your FlexCare account. Type")
                   ($ Text {:style [(tw :text-center :text-gray :px-2)
                                    { :font-size 17
                                     :line-height 22}]} "your Membership ID and Name as it appears")
                   ($ View {:style [(tw :flex :flex-row :items-center :justify-center)]}
                      ($ Text {:style [(tw :text-center :text-gray :px-2)
                                       {:font-size 17
                                        :line-height 22}]} "on your FlexCare ID card.")
                      ($ TouchableOpacity {:onPress #(set-visible true)}
                         ($ View {:style [(tw :w-5 :h-5)]}
                            ($ Svg {:type :info})))) ))

             ($ RegisterForm {& props})

             ($ SignInLink {& props})))))))

(def Screen (with-keechma ScreenRenderer))
