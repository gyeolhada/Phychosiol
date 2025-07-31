package com.example.phychosiolz.main.emotion

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.res.ResourcesCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.Navigation
import androidx.recyclerview.widget.GridLayoutManager
import com.example.phychosiolz.R
import com.example.phychosiolz.data.enums.EmotionType
import com.example.phychosiolz.databinding.FragmentEmotionEditBinding
import com.example.phychosiolz.utils.ViewUtil
import com.example.phychosiolz.view_model.EmotionEditViewModel


class EmotionEditFragment : Fragment() {
    private lateinit var bind: FragmentEmotionEditBinding
    private lateinit var adapter: PhotoSelectRecyclerViewAdapter
    private val viewModel: EmotionEditViewModel by viewModels { EmotionEditViewModel.Factory }

    private lateinit var pickMultipleMedia: ActivityResultLauncher<PickVisualMediaRequest>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        pickMultipleMedia =
            registerForActivityResult(ActivityResultContracts.PickMultipleVisualMedia()) {
                for (uri in it) {
                    requireActivity().contentResolver.takePersistableUriPermission(
                        uri, Intent.FLAG_GRANT_READ_URI_PERMISSION
                    )
                }
                viewModel.submitImages(
                    it.map { uri ->
                        requireActivity().contentResolver.takePersistableUriPermission(
                            uri, Intent.FLAG_GRANT_READ_URI_PERMISSION
                        )
                        uri.toString()
                    }.toMutableList()
                ) {
                    Toast.makeText(requireContext(), "图片最多为9张", Toast.LENGTH_SHORT).show()
                }
            }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        requireActivity().window.statusBarColor =
            ResourcesCompat.getColor(resources, R.color.white, null)
        bind = FragmentEmotionEditBinding.inflate(layoutInflater)

        adapter = PhotoSelectRecyclerViewAdapter(  //屏幕宽度
            (resources.displayMetrics.widthPixels / 4 * 1.2).toInt(),
            {
                //onItemClicked
                //TODO: show image
            }, {
                //onDeleteClicked
                viewModel.removeImage(it)
            }, {
                //onAddClicked
                pickMultipleMedia.launch(
                    PickVisualMediaRequest(
                        ActivityResultContracts.PickVisualMedia.ImageOnly
                    )
                )
            }, requireContext()
        )

        bind.rvImages.adapter = adapter
        bind.rvImages.layoutManager = GridLayoutManager(requireContext(), 3)

        return bind.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        bind.btnEmotionEdit.setOnClickListener {
            clearFocus()
            //Check if the title is empty
            if (viewModel.title.value!!.isEmpty()) {
                bind.edtTitle.error = "标题不能为空"
                return@setOnClickListener
            }
            //Check if the content is empty
            if (viewModel.content.value!!.isEmpty()) {
                bind.edtContent.error = "内容不能为空"
                return@setOnClickListener
            }
            viewModel.saveDiary({
                Toast.makeText(requireContext(), "保存成功", Toast.LENGTH_SHORT).show()
                Navigation.findNavController(it).navigateUp()
            }, {
                Toast.makeText(requireContext(), "保存失败", Toast.LENGTH_SHORT).show()
            })
        }
        //隐藏软键盘
        bind.ll.setOnClickListener {
            it.requestFocus()
            val imm =
                it.context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(it.windowToken, 0)
            clearFocus()
        }
        bind.edtTitle.setOnFocusChangeListener { v, hasFocus ->
            if (!hasFocus) {
                viewModel.title.value = bind.edtTitle.text.toString()
            }
        }
        bind.edtContent.setOnFocusChangeListener { v, hasFocus ->
            if (!hasFocus) {
                viewModel.content.value = bind.edtContent.text.toString()
            }
        }
        bind.ivBack.setOnClickListener {
            Navigation.findNavController(it).navigateUp()
        }

        bind.ivHappy.setOnClickListener {
            viewModel.emotion.value = EmotionType.HAPPY
        }
        bind.ivAngry.setOnClickListener {
            viewModel.emotion.value = EmotionType.ANGRY
        }
        bind.ivSad.setOnClickListener {
            viewModel.emotion.value = EmotionType.SAD
        }
        bind.ivSick.setOnClickListener {
            viewModel.emotion.value = EmotionType.SICK
        }
        bind.ivNeutral.setOnClickListener {
            viewModel.emotion.value = EmotionType.NEUTRAL
        }
        bind.ivScared.setOnClickListener {
            viewModel.emotion.value = EmotionType.SCARED
        }


        viewModel.images.observe(viewLifecycleOwner) {
            Log.d("EmotionEditFragment", "onCreateView: ${viewModel.images.value}")
            adapter.submitList(it)
        }
        viewModel.emotion.observe(viewLifecycleOwner) {
            val theContext = requireContext()
            ViewUtil.setSquareSize(
                theContext,
                if (it == EmotionType.HAPPY) 120f else 50f,
                bind.ivHappy
            )
            ViewUtil.setSquareSize(
                theContext,
                if (it == EmotionType.ANGRY) 120f else 50f,
                bind.ivAngry
            )
            ViewUtil.setSquareSize(
                theContext, if (it == EmotionType.SAD) 120f else 50f, bind.ivSad
            )
            ViewUtil.setSquareSize(
                theContext,
                if (it == EmotionType.SICK) 120f else 50f,
                bind.ivSick
            )
            ViewUtil.setSquareSize(
                theContext,
                if (it == EmotionType.NEUTRAL) 120f else 50f,
                bind.ivNeutral
            )
            ViewUtil.setSquareSize(
                theContext,
                if (it == EmotionType.SCARED) 120f else 50f,
                bind.ivScared
            )
        }
        viewModel.title.observe(viewLifecycleOwner) {
            bind.edtTitle.setText(it)
        }
        viewModel.content.observe(viewLifecycleOwner) {
            bind.edtContent.setText(it)
        }
    }

    private fun clearFocus() {
        bind.edtTitle.clearFocus()
        bind.edtContent.clearFocus()
    }
}