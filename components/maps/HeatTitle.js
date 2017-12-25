import React, {PropTypes, PureComponent} from 'react'
import {PixelRatio, Platform, requireNativeComponent, ViewPropTypes} from 'react-native'
import {LatLng} from '../PropTypes'

export default class HeatTitle extends PureComponent {
  static propTypes = {
    ...ViewPropTypes,

    /**
     * 节点
     */
    coordinates: PropTypes.arrayOf(LatLng).isRequired,


  }

  render() {
    const props = {
      ...this.props,
      ...Platform.select({
        android: {
          strokeWidth: PixelRatio.getPixelSizeForLayoutSize(this.props.strokeWidth),
        },
      }),
    }
    return <AMapHeatTitle {...props}/>
  }
}

const AMapPolygon = requireNativeComponent('AMapHeatTitle', Polygon)
