(ns app.ui.screens.onboarding
  (:require ["react-native" :refer [View TouchableOpacity Animated]]
            ["react-native-keyboard-aware-scroll-view" :refer [KeyboardAwareScrollView]]
            ["react-native-snap-carousel" :refer [default Pagination] :rename {default SnapCarousel}]
            ["lottie-react-native" :as LottieView]
            ["expo-av" :refer [Video]]
            [cljs-bean.core :refer [->clj]]
            [keechma.next.helix.core :refer [with-keechma]]
            [helix.hooks :as hooks]
            [app.hooks :refer [use-dimensions]]
            [app.lib :refer [$ defnc convert-style]]
            [app.tailwind :refer [tw]]
            [app.rn.navigation :refer [navigate]]
            [app.ui.components.screen-container :refer [ScreenContainer]]
            [app.ui.components.buttons :as buttons]
            [app.ui.components.text :as text]
            [app.rn.animated :as a]
            [oops.core :refer [ocall]]))

(def items [{:title "One point of access for all your Digital Health programs"
             :lottie (js/require "../assets/lottie/OnBoarding-1.json")}
            {:title "Access to a Digital Health professional anytime, anywhere"
             :lottie (js/require "../assets/lottie/OnBoarding-2.json")}
            {:title "Skip the waiting room, see a Digital Health provider online"
             :lottie (js/require "../assets/lottie/OnBoarding-3.json")}])

(def common-timing {:to-value 1 :duration 500 :delay 300 :easing (a/easing :ease)})
(def common-top-interpolate-params {:output-range [150 0]})

(def dot-bg-color
  {0 :bg-purple
   1 :bg-yellow
   2 :bg-red})

(defnc PersonButtonView [{:keys [navigation register-button-animate]}]
  ($ a/View {:style [{:opacity @register-button-animate
                      :top (a/interpolate @register-button-animate common-top-interpolate-params)}
                     (tw :w-full :justify-center :items-center :mt-10)]}
     ($ buttons/Big
        {:title "Osobe"
         :onPress #(navigate navigation "person")})))

(defnc ProjectButtonView [{:keys [navigation register-button-animate]}]
       ($ a/View {:style [{:opacity @register-button-animate
                           :top (a/interpolate @register-button-animate common-top-interpolate-params)}
                          (tw :w-full :justify-center :items-center :mt-10)]}
          ($ buttons/Big
             {:title "Projekti"
              :onPress #(navigate navigation "project")})))

(defnc RoleButtonView [{:keys [navigation register-button-animate]}]
       ($ a/View {:style [{:opacity @register-button-animate
                           :top (a/interpolate @register-button-animate common-top-interpolate-params)}
                          (tw :w-full :justify-center :items-center :mt-10)]}
          ($ buttons/Big
             {:title "Uloge"
              :onPress #(navigate navigation "role")})))

(defnc SignInAreaView [{:keys [navigation signin-area-animate]}]
  ($ a/View {:style [{:height 60
                      :opacity @signin-area-animate
                      :top (a/interpolate @signin-area-animate common-top-interpolate-params)}
                     (tw :items-center :justify-center :mb-10)]}
     ($ View {:style [(tw :flex-row)]}
        ($ text/BodyMicro "Already registered? ")
        ($ TouchableOpacity
           {:onPress #(navigate navigation "signin")}
           ($ text/BodyMicro {:style [(tw :underline)]} "Sign In")))))

(defnc ScreenRenderer [props]
  (let [carousel-image-animate (hooks/use-ref (a/value 0))
        carousel-dots-animate (hooks/use-ref (a/value 0))
        carousel-text-animate (hooks/use-ref (a/value 0))
        register-button-animate (hooks/use-ref (a/value 0))
        signin-area-animate (hooks/use-ref (a/value 0))
        on-render-animate (hooks/use-ref (ocall Animated :stagger 200
                                                #js [(a/timing @carousel-image-animate common-timing)
                                                     (a/timing @carousel-dots-animate common-timing)
                                                     (a/timing @carousel-text-animate common-timing)
                                                     (a/timing @register-button-animate common-timing)
                                                     (a/timing @signin-area-animate common-timing)]))]

    (hooks/use-effect :once (-> @on-render-animate
                                (a/start)))

    ($ ScreenContainer
       ($ KeyboardAwareScrollView {:style {:flex 1}}
        ($ View {:style [(tw :items-center :justify-center :px-12 :w-full :h-full)]}
           ($ PersonButtonView {:register-button-animate register-button-animate & props})
           ($ ProjectButtonView {:register-button-animate register-button-animate & props})
           ($ RoleButtonView {:register-button-animate register-button-animate & props})
)))))

(def Screen (with-keechma ScreenRenderer))
