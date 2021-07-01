package com.selim.todoapp.fragments.list

import android.app.AlertDialog
import android.os.Bundle
import android.view.*
import androidx.appcompat.widget.SearchView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.*
import com.google.android.material.snackbar.Snackbar
import com.selim.todoapp.R
import com.selim.todoapp.data.models.ToDoData
import com.selim.todoapp.data.viewmodel.ToDoViewModel
import com.selim.todoapp.databinding.FragmentListBinding
import com.selim.todoapp.fragments.SharedViewModel
import com.selim.todoapp.fragments.list.adapter.ListAdapter
import com.selim.todoapp.utils.hideKeyboard
import com.selim.todoapp.utils.observeOnce
import jp.wasabeef.recyclerview.animators.SlideInUpAnimator


class ListFragment : Fragment(),SearchView.OnQueryTextListener {

    private val adapter: ListAdapter by lazy { ListAdapter() }
    private val mToDoViewModel:ToDoViewModel by viewModels()
    private val mSharedViewModel:SharedViewModel by viewModels()
    private var _binding: FragmentListBinding?=null
    private val binding get()=_binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        _binding=FragmentListBinding.inflate(inflater,container,false)
        binding.lifecycleOwner=this
        binding.mSharedViewModel=mSharedViewModel

        //Setup RecyclerView
        setupRecyclerview()

        //Observe LiveData
        mToDoViewModel.getAllData.observe(viewLifecycleOwner, Observer { data->
            mSharedViewModel.checkIfDatabaseEmpty(data)
            adapter.setData(data)
        })

        // Set Menu
        setHasOptionsMenu(true)
        hideKeyboard(requireActivity())
        return  binding.root
    }

    private fun setupRecyclerview() {
        val recyclerView=binding.recyclerView
        recyclerView.adapter=adapter
        recyclerView.layoutManager=StaggeredGridLayoutManager(2,StaggeredGridLayoutManager.VERTICAL)
        recyclerView.itemAnimator=SlideInUpAnimator().apply {
            addDuration=300
        }
        swipeToDelete(recyclerView)
    }
    private fun swipeToDelete(recyclerView: RecyclerView){
        val swipeToDeleteCallBack=object :SwipeToDelete(){
            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                val deletedItem=adapter.dataList[viewHolder.adapterPosition]
                //Delete Item
                mToDoViewModel.deleteItem(deletedItem)
                adapter.notifyItemRemoved(viewHolder.adapterPosition)

                //Restore Deleted Item
                restoreDeletedData(viewHolder.itemView,deletedItem)
            }
        }
        val  itemTouchHelper=ItemTouchHelper(swipeToDeleteCallBack)
        itemTouchHelper.attachToRecyclerView(recyclerView)
    }

    private fun restoreDeletedData(view: View,deletedItem:ToDoData){
        val snackBar=Snackbar.make(
                view,"Deleted '${deletedItem.title}'",
                Snackbar.LENGTH_LONG
        )
        snackBar.setAction("Undo") {
            mToDoViewModel.insertData(deletedItem)
        }
        snackBar.show()

    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
       inflater.inflate(R.menu.list_fragment_menu,menu)
        val search=menu.findItem(R.id.menu_search)
        val searchView=search.actionView as? SearchView
        searchView?.isSubmitButtonEnabled=true
        searchView?.setOnQueryTextListener(this)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId){
                R.id.menu_delete_all->confirmRemoval()
            R.id.menu_priority_high-> mToDoViewModel.sortByHighPriority.observe(viewLifecycleOwner, Observer { adapter.setData(it) })
            R.id.menu_priority_low->mToDoViewModel.sortByLowPriority.observe(viewLifecycleOwner, Observer { adapter.setData(it) })

        }
        return super.onOptionsItemSelected(item)
    }

    private fun confirmRemoval() {
        val builder= AlertDialog.Builder(requireContext())
        builder.setPositiveButton("Yes"){_,_->
            mToDoViewModel.deleteAll()
            Toast.makeText(requireContext(),"Succesfully Removed",Toast.LENGTH_SHORT).show()
        }
        builder.setNegativeButton("No"){_,_ -> }
        builder.setTitle("Delete Everything?")
        builder.setMessage("Are you sure you want to remove everything?")
        builder.create().show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding=null
    }

    override fun onQueryTextSubmit(query: String?): Boolean {
        if (query!=null){
            searchThroughDatabase(query)
        }
        return true
    }

    override fun onQueryTextChange(newText: String?): Boolean {
        if (newText!=null){
            searchThroughDatabase(newText)
        }
        return true
    }

    private fun searchThroughDatabase(query: String) {
        var searchQuery:String = "%$query%"
        mToDoViewModel.searchDatabase(searchQuery).observeOnce(viewLifecycleOwner, Observer {  list->
           list?.let {
               adapter.setData(it)
           }
        })

    }

}