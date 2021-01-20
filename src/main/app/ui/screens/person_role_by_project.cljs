(ns app.ui.screens.person-role-by-project
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

(defnc AddPersonRoleForm [props]
       (let [project-name (println "")]

            ($ PageContainer
               ($ View {:style [(tw :w-full :px-4 :my-4)]}
                  ($ Text {:style [(tw :text-black :text-center)]} (:project-name props)))

               (wrapped-input {:keechma.form/controller :person-role-edit-project-form
                               :input/type              :select
                               :input/attr              :person
                               :autoCapitalize          "none"})

               (wrapped-input {:keechma.form/controller :person-role-edit-project-form
                               :input/type              :text
                               :input/attr              :role
                               :autoCapitalize          "none"})

               ($ View {:style [(tw :w-full :px-4 :my-2)]}
                  ($ Text {:style [(tw :text-gray-light)]} "Datum dodjele zaduženja"))
               (wrapped-input {:keechma.form/controller :person-role-edit-project-form
                               :input/type              :date
                               :input/attr              :date
                               :placeholder             "OIB"
                               :autoCapitalize          "none"})

               ($ View {:style [(tw "w-full flex flex-row items-center justify-center mt-8")]}
                  ($ buttons/Medium {:onPress #(dispatch props :person-role-edit-project-form :delete)
                                  :title    "Obriši"
                                  :style    [(tw :bg-purple)]
                                  :text-style [(tw :text-white)]})
                  ($ buttons/Medium {:onPress #(dispatch props :person-role-edit-project-form :keechma.form/submit)
                                  :title    "Spremi"
                                  :style    [(tw :bg-purple)]
                                  :text-style [(tw :text-white)]})
                  ))))

(defnc ScreenRenderer [props]
       (let [navigation (useNavigation)
             [visible set-visible] (hooks/use-state false)
             image-uri (:uri (use-sub props :image-upload))
             image-upload-top-value (hooks/use-ref (animated/value 500))
             subtitle-top-value (hooks/use-ref (animated/value 500))
             animate-image-upload (hooks/use-ref (animated/timing @image-upload-top-value {:duration duration :to-value 0}))
             animate-subtitle (hooks/use-ref (animated/timing @subtitle-top-value {:duration duration :to-value 0}))
             project-edit-data (use-sub props :project-edit-form)
             _ (println "______________________________________________" project-edit-data)]

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

                           ($ AddPersonRoleForm {:project-name (get-in project-edit-data [:project-edit :name])
                                                 & props}))))))))

(def Screen (with-keechma ScreenRenderer))
