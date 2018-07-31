package in.p2psystem.bakingapp;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import butterknife.BindView;
import butterknife.ButterKnife;
import in.p2psystem.bakingapp.adapters.RecipeDetailAdapter;
import in.p2psystem.bakingapp.model.Recipe;

public class RecipeDetailFragment extends Fragment implements RecipeDetailAdapter.RecipeDetailClickListener {
    final private static String BUNDLE_KEY = "recipe_to_show";
    final private static String TAG = RecipeDetailFragment.class.getSimpleName();
    final private static String RECYCLER_POS = "recycler_position";
    @Nullable
    @BindView(R.id.recycler_fragment)
    RecyclerView recyclerViewRecipeDetail;
    RecipeDetailAdapter adapter;
    private OnRecipeStepClickListener mListener;
    private Recipe recipe;

    public RecipeDetailFragment() {
        // Required empty public constructor
    }

    public static RecipeDetailFragment newInstance(Recipe recipe) {

        Bundle args = new Bundle();
        args.putParcelable(BUNDLE_KEY, recipe);
        RecipeDetailFragment fragment = new RecipeDetailFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        if (adapter != null)
            outState.putInt(RECYCLER_POS, adapter.getViewHolderPosition());
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof RecipeDetailFragment.OnRecipeStepClickListener) {
            mListener = (RecipeDetailFragment.OnRecipeStepClickListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            recipe = getArguments().getParcelable(BUNDLE_KEY);
        }
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_recipe_detail, container, false);
        ButterKnife.bind(this, view);
        adapter = new RecipeDetailAdapter(getContext(), recipe, this);
        if (recyclerViewRecipeDetail != null) {
            recyclerViewRecipeDetail.setLayoutManager(new LinearLayoutManager(getContext()));
            recyclerViewRecipeDetail.setAdapter(adapter);
        }
        if (savedInstanceState != null)
            recyclerViewRecipeDetail.scrollToPosition(savedInstanceState.getInt(RECYCLER_POS));
        return view;
    }

    @Override
    public void onRecipeStepClick(int position) {
        mListener.onRecipeClicked(position);
    }

    public interface OnRecipeStepClickListener {
        void onRecipeClicked(int position);
    }
}
