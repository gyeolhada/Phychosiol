package com.example.phychosiolz.network

import android.util.Log
import com.example.phychosiolz.model.ManagerTransInfo
import com.google.gson.Gson
import fi.iki.elonen.NanoHTTPD
import java.net.URLDecoder

//用户使用的服务器
class UserHTTPServer(hostname: String?, port: Int) : NanoHTTPD(hostname, port) {

    private var responseDoctor: ManagerTransInfo? = null
    fun clear() {
        responseDoctor = null
    }

    var listener: OnDoctorResponseListener? = null

    interface OnDoctorResponseListener {
        fun onDoctorResponse(response: ManagerTransInfo)
    }


    override fun serve(session: IHTTPSession?): Response {
        return dealWith(session)
    }

    @Synchronized
    private fun dealWith(session: IHTTPSession?): Response {
        //响应get请求
        if (Method.GET == session?.method) {
            if (session.uri == HTTP_URI_HELLO) {
                Log.d("HTTPServer", "hello")
                return responseJsonString(200, "hello", "hello")
            }
            if (session.uri == HTTP_URI_GET_GRAPH) {
                Log.d("HTTPServer", "getGraph")
                UserNetWorkController.addSendGraphIP(session.remoteIpAddress)
                return responseJsonString(200, "getGraph", "getGraph")
            }
        } else if (Method.POST == session?.method) {//响应post请求
            if (session.uri == HTTP_URI_DOCTOR_RESPONSE) {
                Log.d("HTTPServer", "doctorResponse")
                if (responseDoctor != null) {
                    return responseJsonString(404, Gson().toJson(responseDoctor), "exist")
                }
                //处理body
                try {
                    var info = session.queryParameterString
                    info = URLDecoder.decode(info, "UTF-8");
                    Log.d("HTTPServer", "doctorResponse: $info")
                    responseDoctor = Gson().fromJson(info, ManagerTransInfo::class.java)
                    listener?.onDoctorResponse(responseDoctor!!)
                    Log.d("HTTPServer", "doctorResponse: ${responseDoctor!!.name}")
                    return responseJsonString(
                        200,responseDoctor!!.name, MSG_OK)
                } catch (e: Exception) {
                    Log.e("HTTPServer", "doctorResponse: ${e.message}")
                    return responseJsonString(404, "", "Request not support!")
                }
            }
        }
        return responseJsonString(404, "", "Request not support!")
    }

    private fun <T : Any> responseJsonString(code: Int, data: T, msg: String): Response {
        val response = Responser<T>(code, data, msg)
        return newFixedLengthResponse(Gson().toJson(response))//返回对应的响应体Respon
    }
}
