(ns app.ui.screens.signin
  (:require ["react-native" :refer [View Text TouchableOpacity ScrollView Animated]]
            ["@react-navigation/native" :refer [useNavigation]]
            ["react-native-keyboard-aware-scroll-view" :refer [KeyboardAwareScrollView]]
            ["lottie-react-native" :as LottieView]
            [keechma.next.helix.core :refer [with-keechma dispatch use-meta-sub use-sub]]
            [oops.core :refer [oget ocall]]
            [helix.hooks :as hooks]
            [app.lib :refer [$ defnc]]
            [app.tailwind :refer [tw]]
            [app.ui.components.buttons :as buttons]
            [app.ui.components.screen-container :refer [ScreenContainer]]
            [app.rn.navigation :refer [navigate]]
            [app.ui.components.text :as text]
            [app.ui.components.inputs :refer [wrapped-input]]
            [app.rn.animated :as animated]
            [app.util :refer [resolve-error]]
            [app.ui.components.shared :refer [MarginV ButtonBigGray Errors]]))

(def AnimatedView (oget Animated :View))

(def duration 600)

(defnc RegisterPageContainer [{:keys [children]}]
  ($ View {:style [(tw :flex-1 :px-4 :pb-10)]} children))

(defnc SignInForm [props]
  (let [meta-state (use-meta-sub props :sign-in-form)
        backend-errors (:error (use-sub props :sign-in-form))
        email-animated-value (hooks/use-ref (animated/value 100))
        password-animated-value (hooks/use-ref (animated/value 100))
        submit-aminated-value (hooks/use-ref (animated/value 100))
        animate-email (hooks/use-ref (animated/timing @email-animated-value {:duration duration :to-value 0}))
        animate-password (hooks/use-ref (animated/timing @password-animated-value {:duration duration :to-value 0}))
        animate-submit-button (hooks/use-ref (animated/timing @submit-aminated-value {:duration duration :to-value 0}))]

    (hooks/use-effect :once
                      (->
                       (ocall Animated :sequence
                              #js[(ocall Animated :delay 500)
                                  (ocall Animated :stagger 200
                                         #js[@animate-email
                                             @animate-password
                                             @animate-submit-button])])
                       (animated/start)))

    ($ RegisterPageContainer
       (wrapped-input {:keechma.form/controller :sign-in-form
                       :input/type              :text
                       :input/attr              :email
                       :placeholder             "Email"
                       :keyboardType            "email-address"
                       :autoCapitalize          "none"
                       :animation/top @email-animated-value
                       :animation/opacity (animated/interpolate @email-animated-value {:input-range [0 50 100] :output-range [1 0.5 0]})})
       (wrapped-input {:keechma.form/controller :sign-in-form
                       :input/type              :password
                       :input/attr              :password
                       :placeholder             "Password"
                       :animation/top @password-animated-value
                       :animation/opacity (animated/interpolate @password-animated-value {:input-range [0 50 100] :output-range [1 0.5 0]})})

      (when backend-errors
            ($ Errors {:errors backend-errors}))

       ($ AnimatedView {:style [(tw :flex :flex-1 :items-center :mt-8)
                                {:top @submit-aminated-value
                                 :opacity (animated/interpolate @submit-aminated-value {:input-range [0 50 100] :output-range [1 0.5 0]})}]}
          ($ View {:style [(tw "w-8/12")]}
             ($ buttons/Big {:onPress #(dispatch props :sign-in-form :keechma.form/submit)
                               
                               :title    "Sign In"
                               :style    [(tw :bg-purple)]
                               :text-style [(tw :text-white)]}))))))

(defnc RegisterLink [props]
  (let [navigation (useNavigation)
        register-account-value (hooks/use-ref (animated/value 100))
        animate-register-account (hooks/use-ref (animated/timing @register-account-value {:duration duration :to-value 0}))]

    (hooks/use-effect :once
                      (->
                       (ocall Animated :sequence
                              #js[(ocall Animated :delay 1200)
                                  (ocall Animated :stagger 200
                                         #js[@animate-register-account])])
                       (animated/start)))

    ($ AnimatedView {:style [(tw :flex :flex-1 :flex-row :justify-center)
                             {:top @register-account-value
                              :opacity (animated/interpolate @register-account-value {:input-range [0 50 100] :output-range [1 0.5 0]})}]}
       ($ TouchableOpacity
          ($ Text {:style [(tw :text-gray :text-sm)]}
             "Register your FlexCare Account? "))
       ($ TouchableOpacity {:onPress #(navigate navigation "register")}
          ($ Text {:style [(tw :text-gray :text-sm :underline)]}
             "Click Here.")))))

(defnc ForgotPasswordLink [props]
  (let [navigation (useNavigation)
        forgot-password-value (hooks/use-ref (animated/value 100))
        animate-forgot-password (hooks/use-ref (animated/timing @forgot-password-value {:duration duration :to-value 0}))]

    (hooks/use-effect :once
                      (->
                       (ocall Animated :sequence
                              #js[(ocall Animated :delay 1000)
                                  (ocall Animated :stagger 200
                                         #js[@animate-forgot-password])])
                       (animated/start)))

    ($ AnimatedView {:style [(tw :flex :flex-1)
                             {:top @forgot-password-value
                              :opacity (animated/interpolate @forgot-password-value {:input-range [0 50 100] :output-range [1 0.5 0]})}]}
       ($ TouchableOpacity {:style [(tw :flex :flex-1 :items-center)]
                            :onPress #(navigate navigation "forgot-password")}
          ($ Text {:style [(tw :text-gray :text-sm :underline)]}
             "Forgot password?")))))

(defnc Subtitle [{:keys [animated-value]}]
  ($ AnimatedView {:style {:top animated-value
                           :opacity (animated/interpolate animated-value {:input-range [0 50 100] :output-range [1 0.5 0]})}}
     ($ Text {:style [(tw :text-center :text-gray)
                      {:font-size 17
                       :line-height 22}]}
        "Start using your")
     ($ Text {:style [(tw :mb-4 :text-center :text-gray)
                      {:font-size 17
                       :line-height 22}]}
        "Digital Health Plan today.")))

(defnc ScreenTitle [{:keys [animated-value]}]
  ($ AnimatedView {:style [(tw :flex :flex-1 :items-center :justify-center :mb-4 :mt-1 :relative)]
                   :top animated-value
                   :opacity (animated/interpolate animated-value {:input-range [0 50 100] :output-range [1 0.5 0]})}
     ($ text/H2 "Sign In")))

(defnc SignInVideo [{:keys [animated-value]}]
  ($ AnimatedView {:style [(tw :flex :items-center :flex-1 :relative)]
                   :top animated-value
                   :opacity (animated/interpolate animated-value {:input-range [0 50 100] :output-range [1 0.5 0]})}
     ($ LottieView
        {:source (js/require "../assets/lottie/SignIn.json")
         :autoPlay true
         :style {:width 300 :height 275}})))

(defnc ScreenRenderer [props]
  (let [navigation (:navigation props)
        sign-in-video-value (hooks/use-ref (animated/value 300))
        screen-title-value (hooks/use-ref (animated/value 100))
        screen-subtitle-value (hooks/use-ref (animated/value 100))
        animate-sign-in-video (hooks/use-ref (animated/timing @sign-in-video-value {:duration duration :to-value 0}))
        animate-screen-title (hooks/use-ref (animated/timing @screen-title-value {:duration duration :to-value 0}))
        animate-screen-subtitle (hooks/use-ref (animated/timing @screen-subtitle-value {:duration duration :to-value 0}))]

    (hooks/use-effect :once
                      (-> (ocall Animated :stagger 200
                                 #js[@animate-sign-in-video
                                     @animate-screen-title
                                     @animate-screen-subtitle])
                          (animated/start))
                      #(ocall navigation :addListener "beforeRemove" (fn [e]
                                                                       (ocall e :preventDefault))))

    ($ ScreenContainer
       ($ ScrollView {:scrollIndicatorInsets #js{:right 1}}
       ($ KeyboardAwareScrollView {:style {:flex 1}
                                   :enableOnAndroid true
                                   :extraScrollHeight 75
                                   :extraHeight 75
                                   :keyboardShouldPersistTaps "always"}
          ($ RegisterPageContainer
             ($ SignInVideo {:animated-value @sign-in-video-value})
             ($ ScreenTitle {:animated-value @screen-title-value})
             ($ Subtitle {:animated-value @screen-subtitle-value})
             ($ SignInForm {& props})
             ($ MarginV {:spacing 15})
             ($ ForgotPasswordLink {$ props})
             ($ MarginV {:spacing 35})
             ($ RegisterLink {& props})))))))

(def Screen (with-keechma ScreenRenderer))
