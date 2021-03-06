import React, {Component} from 'react'
import {
  View,
  Text,
  FlatList,
  StyleSheet,
} from 'react-native'
import MapView from 'react-native-amap3d'

export default class Events extends Component {
  static navigationOptions = {
    title: '地图事件',
  }

  state = {
    locationEnabled:true,
    logs: [],
  }

  _log(event, data) {
    this.setState({
      logs: [
        {
          key: Date.now(),
          time: new Date().toLocaleString(),
          event: event,
          data: JSON.stringify(data, null, 2),
        },
        ...this.state.logs,
      ],
    })
  }

  _logPressEvent = ({nativeEvent}) => this._log('onPress', nativeEvent)
  _logLongPressEvent = ({nativeEvent}) => this._log('onLongPress', nativeEvent)
  _logLocationEvent = ({nativeEvent}) => this._log('onLocation', nativeEvent)
  _logStatusChangeEvent = ({nativeEvent}) => this._log('onStatusChange', nativeEvent)
  _logStatusChangeCompleteEvent = ({nativeEvent}) => this._log('onStatusChangeComplete', nativeEvent)

  _renderItem = ({item}) =>
    <Text style={styles.logText}>{item.time} {item.event}: {item.data}</Text>

  render() {
    return <View style={styles.body}>
      <MapView
        locationEnabled={this.state.locationEnabled}
        onPress={this._logPressEvent}
        onLongPress={this._logLongPressEvent}
        onLocation={this._logLocationEvent}
        onStatusChange={this._logStatusChangeEvent}
        onStatusChangeComplete={this._logStatusChangeCompleteEvent}
        style={styles.body}/>
      <FlatList style={styles.logs} data={this.state.logs} renderItem={this._renderItem}/>
    </View>
  }
}

const styles = StyleSheet.create({
  body: {
    flex: 1,
  },
  logs: {
    elevation: 8,
    backgroundColor: '#fff',
  },
  logText: {
    paddingLeft: 15,
    paddingRight: 15,
    paddingTop: 10,
    paddingBottom: 10,
  },
})
