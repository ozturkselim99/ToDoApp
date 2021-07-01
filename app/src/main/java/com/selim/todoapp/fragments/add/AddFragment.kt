package com.selim.todoapp.fragments.add

import android.os.Bundle
import android.text.TextUtils
import android.view.*
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.selim.todoapp.R
import com.selim.todoapp.data.models.ToDoData
import com.selim.todoapp.data.viewmodel.ToDoViewModel
import com.selim.todoapp.databinding.FragmentAddBinding
import com.selim.todoapp.fragments.SharedViewModel

class AddFragment : Fragment() {

    private val mTodoViewModel:ToDoViewModel by viewModels()
    private val mSharedViewModel:SharedViewModel by viewModels()

    private var _binding:FragmentAddBinding?=null
    private val binding get()=_binding!!
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        setHasOptionsMenu(true)
        // Inflate the layout for this fragment
         _binding= FragmentAddBinding.inflate(layoutInflater, container, false)
            binding.prioritiesSpinner.onItemSelectedListener=mSharedViewModel.listener
        return binding.root
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.add_fragment_menu,menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId==R.id.menu_add){
            insertDataToDb()
        }
        return super.onOptionsItemSelected(item)
    }

    private fun insertDataToDb() {
        val mTitle=binding.titleEt.text.toString()
        val mPriority=binding.prioritiesSpinner.selectedItem.toString()
        val mDescription=binding.descriptionEt.text.toString()

        val validation=mSharedViewModel.verifyDataFromUser(mTitle,mDescription)
        if (validation){
            //Insert
            val newData=ToDoData(0,mTitle,mSharedViewModel.parsePriority(mPriority),mDescription)
            mTodoViewModel.insertData(newData)
            Toast.makeText(requireContext(),"Successfully added",Toast.LENGTH_SHORT).show()
            findNavController().navigate(R.id.action_addFragment_to_listFragment)
        }
        else
        {
            Toast.makeText(requireContext(),"Please fill out all fileds",Toast.LENGTH_SHORT).show()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding=null
    }

}