import {
  takeEvery
} from 'redux-saga'

import {
  put,
  call
} from 'redux-saga/effects'

import {
  FETCH_INIT_JOB_OBJECT,
  setInitJobObject,
  failToInitJobObject,
} from 'actions/initialize.js'

import getProvider, {
  getProviderName,
  getJobQuery
} from '../providers'



export function* watchFetchInitJobObject() {
  yield call(takeEvery, FETCH_INIT_JOB_OBJECT, initJobObjectFlow)
}

export function* initJobObjectFlow(action) {
  try {
    const providerName = yield call(getProviderName)
    const provider = yield call(getProvider)
    const jobObject = yield call(getJobQuery, provider)
    yield put(setInitJobObject(jobObject))

  } catch (error) {
    yield put(failToInitJobObject(error))
  }
}
