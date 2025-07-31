package com.example.phychosiolz.manager

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.Navigation
import com.bumptech.glide.Glide
import com.example.phychosiolz.R
import com.example.phychosiolz.databinding.FragmentNearByUserBinding
import com.example.phychosiolz.network.ManagerNetworkController
import com.example.phychosiolz.view_model.NearByUsersViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import okhttp3.Callback
import okio.IOException

class NearByUserFragment : Fragment() {
    private lateinit var bind: FragmentNearByUserBinding
    private val viewModel: NearByUsersViewModel by viewModels { NearByUsersViewModel.Factory }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        bind = FragmentNearByUserBinding.inflate(layoutInflater)
        bind.list.adapter = NearByUserRecycleViewAdapter {
            bind.list.isEnabled = false//防止重复点击
            ManagerNetworkController.sendRequestForGraph(
                it.userIp,
                object : Callback {
                    override fun onFailure(call: okhttp3.Call, e: IOException) {
                        runBlocking {
                            withContext(Dispatchers.Main) {
                                bind.list.isEnabled = true
                            }
                        }
                        Log.d("NearByUserFragment", "onFailure:${e.message}")
                    }

                    override fun onResponse(call: okhttp3.Call, response: okhttp3.Response) {
                        runBlocking {
                            withContext(Dispatchers.Main) {
                                bind.list.isEnabled = true
                                Navigation.findNavController(bind.root)
                                    .navigate(R.id.action_nearByUserFragment_to_othersOverviewFragment)
                            }
                        }
                        Log.d("NearByUserFragment", "onResponse: ")
                    }
                })
        }
        return bind.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel.setup(context)
        Glide.with(requireContext()).asGif().load(R.drawable.radar).into(bind.ivAvatar)
        viewModel.broadcastResponseIPData.observe(viewLifecycleOwner) {
            (bind.list.adapter as NearByUserRecycleViewAdapter).submitList(it)
        }
    }
}