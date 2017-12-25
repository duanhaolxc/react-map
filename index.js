import MapView from './components/maps/MapView'
import Marker from './components/maps/Marker'
import Polyline from './components/maps/Polyline'
import Polygon from './components/maps/Polygon'
import Circle from './components/maps/Circle'
import HeatTitle from './components/maps/HeatTitle'
import Navigation from './components/navigation'
import MapUtils from './components/Utils'

MapView.Marker = Marker
MapView.Polyline = Polyline
MapView.Polygon = Polygon
MapView.Circle = Circle
MapView.HeatTitle = HeatTitle

export default MapView
export {
  MapView,
  Marker,
  Polyline,
  HeatTitle,
  Polygon,
  Circle,
  Navigation,
  MapUtils,
}
