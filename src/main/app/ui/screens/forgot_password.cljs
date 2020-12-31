(ns app.ui.screens.forgot-password
  (:require ["react-native" :refer [View Text TouchableOpacity Animated]]
            ["lottie-react-native" :as LottieView]
            [keechma.next.helix.core :refer [with-keechma dispatch use-meta-sub use-sub]]
            [app.lib :refer [$ defnc]]
            [app.tailwind :refer [tw]]
            [helix.hooks :as hooks]
            [app.rn.navigation :refer [navigate]]
            [app.ui.components.screen-container :refer [ScreenContainer]]
            [app.ui.components.buttons :as buttons]
            [app.rn.navigation :refer [navigate]]
            ["@react-navigation/native" :refer [useNavigation]]
            ["expo-av" :refer [Video]]
            [app.ui.components.text :as text]
            [app.ui.components.inputs :refer [wrapped-input]]
            ["react-native-keyboard-aware-scroll-view" :refer [KeyboardAwareScrollView]]
            [app.ui.components.carousel :refer [Carousel]]
            [oops.core :refer [oget ocall]]
            [keechma.next.controllers.form :as form]
            [app.rn.animated :as animated]
            [app.util :refer [resolve-error]]
            [app.ui.components.shared :refer [MarginV ButtonBigGray Errors]]))

(def AnimatedView (oget Animated :View))

(def duration 600)

(defnc ForgetPasswordPageContainer [{:keys [children]}]
  ($ View {:style [(tw :flex-1 :px-4 :pb-10)]} children))

(defnc ForgotPasswordForm [props]
  (let [meta-state (use-meta-sub props :forgot-password-form)
        backend-errors {:error (use-sub props :forgot-password-form)}
        email-animated-value (hooks/use-ref (animated/value 100))
        submit-aminated-value (hooks/use-ref (animated/value 100))
        animate-email (hooks/use-ref (animated/timing @email-animated-value {:duration duration :to-value 0}))
        animate-submit-button (hooks/use-ref (animated/timing @submit-aminated-value {:duration duration :to-value 0}))]

    (hooks/use-effect :once
                      (->
                       (ocall Animated :sequence
                              #js[(ocall Animated :delay 500)
                                  (ocall Animated :stagger 200
                                         #js[@animate-email
                                             @animate-submit-button])])
                       (animated/start)))

    ($ ForgetPasswordPageContainer
       (wrapped-input {:keechma.form/controller :forgot-password-form
                       :input/type              :text
                       :input/attr              :email
                       :placeholder             "Email"
                       :animation/top @email-animated-value
                       :animation/opacity (animated/interpolate @email-animated-value {:input-range [0 50 100] :output-range [1 0.5 0]})})

       (when backend-errors
         ($ Errors {:errors backend-errors}))

       ($ AnimatedView {:style [(tw :flex :flex-1 :items-center :mt-8)
                                {:top @submit-aminated-value
                                 :opacity (animated/interpolate @submit-aminated-value {:input-range [0 50 100] :output-range [1 0.5 0]})}]}
          ($ View {:style [(tw "w-8/12")]}
             ($ ButtonBigGray {:on-press #(dispatch props :forgot-password-form :keechma.form/submit)
                               :title    "Send"
                               :style    [(tw :bg-purple)]
                               :text-style [(tw :text-white)]}))))))

(defnc ForgotPasswordVideo [{:keys [animated-value]}]
  ($ AnimatedView {:style [(tw :flex :items-center :flex-1 :relative)]
                   :top animated-value
                   :opacity (animated/interpolate animated-value {:input-range [0 50 100] :output-range [1 0.5 0]})}
     ($ LottieView
        {:source (js/require "../assets/lottie/ForgotPassword.json")
         :autoPlay true
         :resizeMode "contain"
         :style {:width 250 :height 250}})))

(defnc ScreenTitle [{:keys [animated-value]}]
  ($ AnimatedView {:style [(tw :flex :flex-1 :items-center :justify-center :mb-6 :mt-12 :relative)]
                   :top animated-value
                   :opacity (animated/interpolate animated-value {:input-range [0 50 100] :output-range [1 0.5 0]})}
     ($ text/H2 "Forgot Password?")))

(defnc Subtitle [{:keys [animated-value]}]
  ($ AnimatedView {:style {:top animated-value
                           :opacity (animated/interpolate animated-value {:input-range [0 50 100] :output-range [1 0.5 0]})}}
     ($ Text {:style [(tw :text-center :text-gray)
                      {:font-size 17
                       :line-height 22}]}
        "Enter your email address here to receive")
     ($ Text {:style [(tw :mb-4 :text-center :text-gray)
                      {:font-size 17
                       :line-height 22}]}
        "password reset instructions.")))

(defnc ScreenRenderer [props]
  (let [forgot-pass-video-value (hooks/use-ref (animated/value 300))
        screen-title-value (hooks/use-ref (animated/value 100))
        screen-subtitle-value (hooks/use-ref (animated/value 100))
        animate-forgot-pass-video (hooks/use-ref (animated/timing @forgot-pass-video-value {:duration duration :to-value 0}))
        animate-screen-title (hooks/use-ref (animated/timing @screen-title-value {:duration duration :to-value 0}))
        animate-screen-subtitle (hooks/use-ref (animated/timing @screen-subtitle-value {:duration duration :to-value 0}))]

    (hooks/use-effect :once
                      (-> (ocall Animated :stagger 200
                                 #js[@animate-forgot-pass-video
                                     @animate-screen-title
                                     @animate-screen-subtitle])
                          (animated/start)))
    ($ ScreenContainer
       ($ KeyboardAwareScrollView {:style {:flex 1}
                                   :enableOnAndroid true
                                   :extraScrollHeight 75
                                   :extraHeight 75
                                   :keyboardShouldPersistTaps "always"}
          ($ ForgotPasswordVideo {:animated-value @forgot-pass-video-value})
          ($ ScreenTitle {:animated-value @screen-title-value})
          ($ Subtitle {:animated-value @screen-subtitle-value})
          ($ ForgotPasswordForm {& props})))))

(def Screen (with-keechma ScreenRenderer))
