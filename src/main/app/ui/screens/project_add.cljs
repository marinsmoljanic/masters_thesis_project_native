(ns app.ui.screens.project-add
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

(defnc PageContainer [{:keys [children]}]
       ($ View {:style [(tw :h-full :w-full :flex :flex-1 :px-4 :pb-10 :mt-4)]} children))

(defnc AddProjectForm [props]
       (let [meta-state (use-meta-sub props :project-form)
             backend-errors (:error (use-sub props :project-form))]

            ($ PageContainer
               (wrapped-input {:keechma.form/controller :project-form
                               :input/type              :text
                               :input/attr              :name
                               :placeholder             "Naziv projekta"
                               :autoCapitalize          "none"})

               (wrapped-input {:keechma.form/controller :project-form
                               :input/type              :text
                               :input/attr              :description
                               :placeholder             "Opis projekta"
                               :autoCapitalize          "none"})

               ($ View {:style [(tw :w-full :px-4 :mt-2)]}
                  ($ Text {:style [(tw :text-gray-light)]} "Datum početka"))
               (wrapped-input {:keechma.form/controller :project-form
                               :input/type              :date
                               :input/attr              :startDate
                               :placeholder             "Datum pocetka"
                               :autoCapitalize          "none"})
               ($ View {:style [(tw :w-full :px-4 :my-2)]}
                  ($ Text {:style [(tw :text-gray-light)]} "Datum završetka"))
               (wrapped-input {:keechma.form/controller :project-form
                               :input/type              :date
                               :input/attr              :endDate
                               :placeholder             "Datum zavrsetka"
                               :autoCapitalize          "none"})

               (when backend-errors
                     ($ Errors {:errors backend-errors}))

               ($ View {:style [(tw "w-full items-center justify-center mt-8")]}
                  ($ buttons/Big {:onPress #(dispatch props :project-form :keechma.form/submit)
                                  :title    "Spremi"
                                  :style    [(tw :bg-purple)]
                                  :text-style [(tw :text-white)]})))))

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
                  ($ KeyboardAwareScrollView
                     {:style [(tw :flex :flex-1)]
                      :enableOnAndroid true
                      :extraScrollHeight 75
                      :extraHeight 75
                      :keyboardShouldPersistTaps "always"}
                     ($ PageContainer
                        ($ AnimatedView {:style [(tw :relative)
                                                 {:top @subtitle-top-value
                                                  :opacity (animated/interpolate @subtitle-top-value {:input-range [0 25 50] :output-range [1 0.5 0]})}]}

                           ($ AddProjectForm {& props}))))))))

(def Screen (with-keechma ScreenRenderer))
