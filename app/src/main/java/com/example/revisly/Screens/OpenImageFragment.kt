package com.example.revisly.Screens

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.Toast
import com.example.revisly.Adapters.Features
import com.example.revisly.Adapters.OpenImageAdapter
import com.example.revisly.FlattenedImage
import com.example.revisly.MainActivity
import com.example.revisly.R
import com.example.revisly.RoomDatabase.Data
import com.example.revisly.SavesData
import com.example.revisly.databinding.FragmentOpenImageBinding
import com.google.android.material.bottomsheet.BottomSheetDialog

class OpenImageFragment : Fragment() {

    lateinit var binding: FragmentOpenImageBinding
    lateinit var db : Data
    lateinit var adapter : OpenImageAdapter
    lateinit var flattenedImages : ArrayList<SavesData>


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {

        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentOpenImageBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        (activity as MainActivity)?.binding?.bottomBar?.visibility = View.GONE
        db = Data.getInstance(requireContext())


        flattenedImages = arguments?.getParcelableArrayList<SavesData>("Savelist") ?: return
        val startIndex = arguments?.getInt("startIndex") ?: 0
//        val groupedItems: List<List<FlattenedImage>> = flattenedImages
//            .groupBy { it.itemIndex }
//            .toSortedMap() // ensures item order is preserved
//            .values
//            .toList()

        adapter = OpenImageAdapter(flattenedImages, object : Features{
            override fun toggleFaviorite(position: Int,img : ImageView) {
                Log.e("The position is ", "toggelfav: $position", )
                val item = flattenedImages[position]
                // Step 1: Animate out (shrink + fade)
                img.animate()
                    .scaleX(0f)
                    .scaleY(0f)
                    .alpha(0f)
                    .setDuration(150)
                    .withEndAction {
                        // Step 2: Change the image
                        item.favoraite = !item.favoraite
                        img.setImageResource(
                            if (item.favoraite) R.drawable.ic_favorite_filled else R.drawable.ic_favorite
                        )

                        // Step 3: Animate in (grow + fade)
                        img.animate()
                            .scaleX(1f)
                            .scaleY(1f)
                            .alpha(1f)
                            .setDuration(150)
                            .start()
                        db.inter().UpdateSave(item)
                        adapter.notifyItemChanged(position)
                    }
                    .start()

            }

            override fun delteitem(position: Int) {
                showDeleteConfirmationDialog(position){

                }
            }

            override fun shareitem(position: Int) {
                val sendIntent = Intent().apply {
                    action = Intent.ACTION_SEND
                    putExtra(Intent.EXTRA_TEXT, flattenedImages[position].url)
                    type = "text/plain"
                }

                val shareIntent = Intent.createChooser(sendIntent, "Share via")
                requireContext().startActivity(shareIntent)
            }

            override fun entertags(position: Int) {
//                TODO("Not yet implemented")
            }

            override fun toggelarchived(position: Int) {
                val item = flattenedImages[position]
                item.archived = !item.archived

                if(item.archived){
                    Toast.makeText(requireContext(), "item is Archieved", Toast.LENGTH_SHORT).show()
                }else{
                    Toast.makeText(requireContext(), "item is unArchieved", Toast.LENGTH_SHORT).show()
                }

                db.inter().UpdateSave(item)
                adapter.notifyItemChanged(position)


            }

        })

        binding.ShowsImageHandler.adapter = adapter
        binding.ShowsImageHandler.setCurrentItem(startIndex, false)



    }

    fun showDeleteConfirmationDialog(position : Int, onDeleteConfirmed: () -> Unit) {
        val builder = AlertDialog.Builder(requireContext())
        builder.setTitle("Delete Item")
        builder.setMessage("Are you sure you want to delete this item?")

        builder.setPositiveButton("Yes") { dialog, _ ->
            db.inter().DeleteSave(flattenedImages[position])
            flattenedImages.removeAt(position)
            adapter.notifyItemRemoved(position)
            dialog.dismiss()
        }

        builder.setNegativeButton("No") { dialog, _ ->
            dialog.dismiss()
        }

        val dialog = builder.create()
        dialog.show()
    }


    companion object {

        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            OpenImageFragment().apply {
                arguments = Bundle().apply {

                }
            }
    }

    override fun onDestroy() {
        super.onDestroy()
        (activity as MainActivity)?.binding?.bottomBar?.visibility = View.VISIBLE
    }
}