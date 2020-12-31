(ns app.ui.components.carousel
  (:require ["react-native" :refer [View]]
            ["react-native-snap-carousel" :refer [default Pagination] :rename {default SnapCarousel}]
            [cljs-bean.core :refer [->clj]]
            [helix.hooks :as hooks]
            [app.lib :refer [$ defnc convert-style]]
            [app.hooks :refer [use-dimensions]]
            [app.ui.components.text :as text]
            [app.tailwind :refer [tw]]
            ["expo-av" :refer [Video]]))

(def index-bg-color
  {0 :bg-purple
   1 :bg-yellow
   2 :bg-red})

(defnc CarouselItem [{:keys [items item index active-item-index]}]
  ($ View {:style [{:flex 1}]}
     ($ View {:style [{:height 440} (tw :items-center :justify-center :w-full)]}
        ($ Video {:isMuted true
                  :shouldPlay (= index active-item-index)
                  :isLooping true
                  :resizeMode "contain"
                  :style [(tw :h-full :w-full)]
                  :source (:video item)}))
     ($ View {:style [{:height 70} (tw :items-center :justify-center)]}
        ($ Pagination
           {:dotsLength (count items)
            :activeDotIndex index
            :dotStyle (convert-style (tw :rounded-full :w-3 :h-3 (index-bg-color index)))
            :inactiveDotStyle (convert-style (tw :bg-gray-light))
            :inactiveDotScale 1}))
     ($ View {:style [{:height 160} (tw :items-center :justify-start :px-10)]}
        ($ text/H2 {:style (tw :text-center)} (:title item)))))

(defnc Carousel [{:keys [items]}]
  (let [[active-item-index set-active-item-index] (hooks/use-state 0)
        {:keys [width]} (use-dimensions)
        carousel-ref (hooks/use-ref nil)]
    ($ View
       ($ SnapCarousel
          {:data (into-array items)
           :ref carousel-ref
           :renderItem (fn [props]
                         (let [props' (->clj props)
                               index (:index props')]
                           ($ CarouselItem {:items items
                                            :item (:item props')
                                            :index index
                                            :active-item-index active-item-index})))
           :onBeforeSnapToItem #(set-active-item-index %)
           :sliderWidth width
           :itemWidth width}))))
