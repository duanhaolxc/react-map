import {requireNativeComponent, ViewPropTypes} from 'react-native'

/**
 * 用于自定义标记
 */
export default requireNativeComponent('AMapOverlay', {
  propTypes: {
    ...ViewPropTypes,
  }
})
