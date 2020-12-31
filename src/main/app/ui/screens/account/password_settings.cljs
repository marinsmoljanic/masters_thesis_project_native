(ns app.ui.screens.account.password-settings
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

(defnc UpdatePasswordPageContainer [{:keys [children]}]
  ($ View {:style [(tw :flex-1 :px-4)]} children))

(defnc ForgotPasswordForm [props]
  (let [meta-state (use-meta-sub props :update-password-form)
        password-value (get-in (form/get-form-data meta-state) [:data :password])
        password2-value (get-in (form/get-form-data meta-state) [:data :password2])
        password-icon-end (if (and (not (empty? password-value)) (= password-value password2-value)) :green-check :none)
        backend-errors (:error (use-sub props :update-password-form))
        old-password-animated-value (hooks/use-ref (animated/value 100))
        new-password-animated-value (hooks/use-ref (animated/value 100))
        confirm-password-animated-value (hooks/use-ref (animated/value 100))
        submit-aminated-value (hooks/use-ref (animated/value 100))
        animate-old-password (hooks/use-ref (animated/timing @old-password-animated-value {:duration duration :to-value 0}))
        animate-new-password (hooks/use-ref (animated/timing @new-password-animated-value {:duration duration :to-value 0}))
        animate-confirm-password (hooks/use-ref (animated/timing @confirm-password-animated-value {:duration duration :to-value 0}))
        animate-submit-button (hooks/use-ref (animated/timing @submit-aminated-value {:duration duration :to-value 0}))]

    (hooks/use-effect :once
                      (->
                       (ocall Animated :sequence
                              #js[(ocall Animated :delay 500)
                                  (ocall Animated :stagger 200
                                         #js[@animate-old-password
                                             @animate-new-password
                                             @animate-confirm-password
                                             @animate-submit-button])])
                       (animated/start)))

    ($ UpdatePasswordPageContainer
       (wrapped-input {:keechma.form/controller :update-password-form
                       :input/type              :password
                       :input/attr              :old-password
                       :placeholder             "Old Password"
                       :icon-end :none
                       :animation/top @old-password-animated-value
                       :animation/opacity (animated/interpolate @old-password-animated-value {:input-range [0 50 100] :output-range [1 0.5 0]})})
       (wrapped-input {:keechma.form/controller :update-password-form
                       :input/type              :password
                       :input/attr              :password
                       :placeholder             "New Password"
                       :icon-end password-icon-end
                       :animation/top @new-password-animated-value
                       :animation/opacity (animated/interpolate @new-password-animated-value {:input-range [0 50 100] :output-range [1 0.5 0]})})
       (wrapped-input {:keechma.form/controller :update-password-form
                       :input/type              :password
                       :input/attr              :password2
                       :placeholder             "Confirm Password"
                       :icon-end password-icon-end
                       :animation/top @confirm-password-animated-value
                       :animation/opacity (animated/interpolate @confirm-password-animated-value {:input-range [0 50 100] :output-range [1 0.5 0]})})

       (when backend-errors
         ($ Errors {:errors backend-errors}))

       ($ AnimatedView {:style [(tw :flex :flex-1 :items-center :mt-8)
                                {:top @submit-aminated-value
                                 :opacity (animated/interpolate @submit-aminated-value {:input-range [0 50 100] :output-range [1 0.5 0]})}]}
          ($ View {:style [(tw "w-8/12")]}
             ($ buttons/Big {:onPress #(dispatch props :update-password-form :keechma.form/submit)
                               :title    "Reset"
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
  ($ AnimatedView {:style [(tw :flex :flex-1 :items-center :justify-center :mb-6 :mt-3 :relative)]
                   :top animated-value
                   :opacity (animated/interpolate animated-value {:input-range [0 50 100] :output-range [1 0.5 0]})}
     ($ text/H2 "Update Password")))

(defnc ScreenRenderer [props]
  (let [forgot-pass-video-value (hooks/use-ref (animated/value 300))
        screen-title-value (hooks/use-ref (animated/value 100))
        animate-forgot-pass-video (hooks/use-ref (animated/timing @forgot-pass-video-value {:duration duration :to-value 0}))
        animate-screen-title (hooks/use-ref (animated/timing @screen-title-value {:duration duration :to-value 0}))]

    (hooks/use-effect :once
                      (-> (ocall Animated :stagger 200
                                 #js[@animate-forgot-pass-video
                                     @animate-screen-title])
                          (animated/start)))
    ($ ScreenContainer
       ($ KeyboardAwareScrollView {:style {:flex 1}
                                   :enableOnAndroid true
                                   :extraScrollHeight 75
                                   :extraHeight 75
                                   :keyboardShouldPersistTaps "always"}
          ($ ForgotPasswordVideo {:animated-value @forgot-pass-video-value})
          ($ ScreenTitle {:animated-value @screen-title-value})
          ($ ForgotPasswordForm {& props})))))

(def Screen (with-keechma ScreenRenderer))
