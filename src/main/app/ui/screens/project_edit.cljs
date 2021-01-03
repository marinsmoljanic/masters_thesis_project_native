(ns app.ui.screens.project-edit
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
       ($ View {:style [(tw :h-full :w-full :flex :flex-1 :px-4 :pb-10 :mt-2)]} children))

(defnc TableHeader [_]
       ($ View {:style [(tw :flex :flex-row :bg-purple :mt-4)]}
          ($ View {:style [(tw :flex :items-center :justify-center :pt-2 :pb-2 :border-r-2 :border-white :border-solid) {:width "33.33333%"}]}
             ($ Text {:style [(tw :text-white :px-2 :text-center)
                              {:font-size 17
                               :line-height 22}]} "Osoba"))
          ($ View {:style [(tw :flex :items-center :justify-center :pt-2 :pb-2 ) {:width "33.33333%"}]}
             ($ Text {:style [(tw :text-white)
                              {:font-size 17
                               :line-height 22}]} "Uloga"))
          ($ View {:style [(tw :flex :items-center :justify-center :pt-2 :pb-2 :border-l-2 :border-white :border-solid) {:width "33.33333%"}]}
             ($ Text {:style [(tw :text-white)
                              {:font-size 17
                               :line-height 22}]} "Datum zaduzenja"))))

(defnc TableItem [props]
       (let [_ (+ 1 1)]
            ($ View {:style [(tw :flex :flex-row)]}
               ($ View {:style [(tw :flex :items-start :justify-center :pt-2 :pb-2
                                    :border-l :border-r :border-b :border-gray-light :border-solid) {:width "33.33333%"}]}
                  ($ Text {:style [(tw :text-black :px-2)
                                   {:font-size 17
                                    :line-height 22}]} (:person props)))
               ($ View {:style [(tw :flex :items-start :justify-center :pt-2 :pb-2
                                    :border-b :border-gray-light :border-solid) {:width "33.33333%"}]}
                  ($ Text {:style [(tw :text-black :px-2)
                                   {:font-size 17
                                    :line-height 22}]} (:role props)))
               ($ View {:style [(tw :flex :items-start :justify-center :pt-2 :pb-2
                                    :border-l :border-r :border-b :border-gray-light :border-solid) {:width "33.33333%"}]}
                  ($ Text {:style [(tw :text-black :px-2)
                                   {:font-size 17
                                    :line-height 22}]} (:assignmentDate props))))))

(defnc EditProjectForm [props]
       (let [meta-state (use-meta-sub props :-form)
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

               (wrapped-input {:keechma.form/controller :project-form
                               :input/type              :text
                               :input/attr              :startDate
                               :placeholder             "Datum pocetka"
                               :autoCapitalize          "none"})

               (wrapped-input {:keechma.form/controller :project-form
                               :input/type              :text
                               :input/attr              :endDate
                               :placeholder             "Datum zavrsetka"
                               :autoCapitalize          "none"})
               (when backend-errors
                     ($ Errors {:errors backend-errors}))

               ($ View {:style [(tw "flex flex-row w-full items-center justify-center mt-8")]}
                  ($ buttons/Medium {:onPress #(dispatch props :project-form :keechma.form/submit)
                                     :title    "Obrisi"
                                     :style    [(tw :bg-purple)]
                                     :text-style [(tw :text-white)]})
                  ($ buttons/Medium {:onPress #(dispatch props :project-form :keechma.form/submit)
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
             person-role-mock-data [{:person "Ivica"  :role "Developer"  :assignmentDate "11.12.2020"}
                                    {:person "Franc"  :role "QA"         :assignmentDate "04.05.2020"}
                                    {:person "Lepej"  :role "Lead"       :assignmentDate "01.01.2020"}
                                    {:person "Stanga" :role "Support"    :assignmentDate "11.12.2020"}]]

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
                           ($ EditProjectForm {& props})
                           ($ TableHeader)
                           (map (fn [person-role]
                                    ($ TableItem {:person         (:person person-role)
                                                  :role           (:role person-role)
                                                  :assignmentDate (:assignmentDate person-role)
                                                  :key            (gensym 10)}))
                                person-role-mock-data))))))))

(def Screen (with-keechma ScreenRenderer))