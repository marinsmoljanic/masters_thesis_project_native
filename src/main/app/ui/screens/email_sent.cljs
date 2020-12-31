(ns app.ui.screens.email-sent
  (:require ["react-native" :refer [View Text Animated]]
            ["@react-navigation/native" :refer [useNavigation]]
            ["lottie-react-native" :as LottieView]
            ["react-native-keyboard-aware-scroll-view" :refer [KeyboardAwareScrollView]]
            [keechma.next.helix.core :refer [with-keechma]]
            [oops.core :refer [oget ocall]]
            [helix.hooks :as hooks]
            [app.lib :refer [$ defnc]]
            [app.tailwind :refer [tw]]
            [app.rn.animated :as animated]
            [app.rn.navigation :refer [navigate]]
            [app.ui.components.text :as text]
            [app.ui.components.shared :refer [ButtonBigGray]]
            [app.ui.components.screen-container :refer [ScreenContainer]]))

(def AnimatedView (oget Animated :View))

(def duration 600)

(defnc EmailSentVideo [{:keys [animated-value]}]
  ($ AnimatedView {:style [(tw :flex :items-center :flex-1 :relative)]
                   :top animated-value
                   :opacity (animated/interpolate animated-value {:input-range [0 50 100] :output-range [1 0.5 0]})}
     ($ LottieView
        {:source (js/require "../assets/lottie/EmailSent.json")
         :autoPlay true
         :resizeMode "contain"
         :style {:width 200 :height 200}})))

(defnc ScreenTitle [{:keys [animated-value]}]
  ($ AnimatedView {:style [(tw :flex :flex-1 :items-center :justify-center :mb-6 :mt-12 :relative)]
                   :top animated-value
                   :opacity (animated/interpolate animated-value {:input-range [0 50 100] :output-range [1 0.5 0]})}
     ($ text/H2 "Email Sent!")))

(defnc Subtitle [{:keys [animated-value]}]
  ($ AnimatedView {:style {:top animated-value
                           :opacity (animated/interpolate animated-value {:input-range [0 50 100] :output-range [1 0.5 0]})}}
     ($ Text {:style [(tw :text-center :text-gray)
                      {:font-size 17
                       :line-height 22}]}
        "We’ve sent you a reset password email, click")
     ($ Text {:style [(tw :text-center :text-gray)
                      {:font-size 17
                       :line-height 22}]}
        "on the link within the email and follow the")
     ($ Text {:style [(tw :mb-4 :text-center :text-gray)
                      {:font-size 17
                       :line-height 22}]}
        "instructions to reset your password.")))

(defn ResendButton [props]
  (let [navigation (useNavigation)
        resend-button-value (hooks/use-ref (animated/value 100))
        animate-resend-button (hooks/use-ref (animated/timing @resend-button-value {:duration duration :to-value 0}))]

    (hooks/use-effect :once
                      (->
                       (ocall Animated :sequence
                              #js[(ocall Animated :delay 700)
                                  (ocall Animated :stagger 200
                                         #js[@animate-resend-button])])
                       (animated/start)))

    ($ AnimatedView {:style [(tw :flex :flex-1 :items-center :mt-8)
                             {:top @resend-button-value
                              :opacity (animated/interpolate @resend-button-value {:input-range [0 50 100] :output-range [1 0.5 0]})}]}
       ($ View {:style [(tw "w-8/12")]}
          ($ ButtonBigGray {:on-press #(navigate navigation "forgot-password")
                            :title    "Resend verification email"
                            :style    [(tw :bg-purple)]
                            :text-style [(tw :text-white)]})))))

(defnc ScreenRenderer [props]
  (let [email-sent-video-value (hooks/use-ref (animated/value 300))
        screen-title-value (hooks/use-ref (animated/value 100))
        screen-subtitle-value (hooks/use-ref (animated/value 100))
        animate-email-sent-video (hooks/use-ref (animated/timing @email-sent-video-value {:duration duration :to-value 0}))
        animate-screen-title (hooks/use-ref (animated/timing @screen-title-value {:duration duration :to-value 0}))
        animate-screen-subtitle (hooks/use-ref (animated/timing @screen-subtitle-value {:duration duration :to-value 0}))]

    (hooks/use-effect :once
                      (-> (ocall Animated :stagger 200
                                 #js[@animate-email-sent-video
                                     @animate-screen-title
                                     @animate-screen-subtitle])
                          (animated/start)))
    ($ ScreenContainer
       ($ KeyboardAwareScrollView {:style {:flex 1}}
          ($ EmailSentVideo {:animated-value @email-sent-video-value})
          ($ ScreenTitle {:animated-value @screen-title-value})
          ($ Subtitle {:animated-value @screen-subtitle-value})
          ($ ResendButton {& props})))))

(def Screen (with-keechma ScreenRenderer))
