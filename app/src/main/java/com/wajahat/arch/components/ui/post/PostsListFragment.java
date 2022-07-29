package com.wajahat.arch.components.ui.post;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.StringRes;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.RecyclerView;

import com.wajahat.arch.components.R;
import com.wajahat.arch.components.data.model.Post;
import com.wajahat.arch.components.data.model.PostAdapterModel;
import com.wajahat.arch.components.data.model.PostHeader;
import com.wajahat.arch.components.data.repository.PostsRepository;
import com.wajahat.arch.components.ui.base.ViewModelFactory;
import com.wajahat.arch.components.utils.ConnectionUtils;
import com.wajahat.arch.components.utils.OnPostClickListener;
import com.wajahat.arch.components.utils.PostUtils;
import com.wajahat.arch.components.utils.Status;
import com.wajahat.arch.components.utils.VerticalItemDecoration;

import java.util.ArrayList;
import java.util.List;

public class PostsListFragment extends Fragment implements OnPostClickListener {

    /**
     * Although in a practical world, we usually initialize the objects using some Dependency Injection framework
     * like Dagger|Hilt. But since, for the scope of this test, as we need to keep the things simplified and avoid
     * rewriting everything from the scratch as mentioned in the YOUR TASK, therefore following approach is also a
     * good one
     */
    private PostsViewModel mViewModel;
    private final PostsRepository mPostsRepository = new PostsRepository();

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mViewModel = new ViewModelProvider(this, new ViewModelFactory(mPostsRepository)).get(PostsViewModel.class);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.posts_fragment, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        fetchPosts();
    }

    private void fetchPosts() {
        // Verify if Internet is present
        if (!ConnectionUtils.INSTANCE.isNetworkAvailable(requireContext())) {
            hideLoadingAnimation();
            showErrorView(R.string.no_internet);
            return;
        }
        mViewModel.getPosts().observe(getViewLifecycleOwner(), response -> {
            if (response.getStatus() == Status.SUCCESS) {
                // If though API succeeded, but maybe no posts are present for now, then we need to display the
                // error message
                if (response.getData() == null || response.getData().getPosts() == null) {
                    showErrorView(R.string.no_data_found);
                    return;
                }

                // Posts list fetched from the API
                List<Post> responsePosts = response.getData().getPosts();

                // Populating the generic list of #PostAdapterModel so as to handle the header views as well
                List<PostAdapterModel> formattedPosts = new ArrayList<>();

                // With every header supposed to be added to the #formattedPosts list, we must increase the loop
                // iterations limit.
                int formattedPostsSize = responsePosts.size();

                // Since we are populating the #formattedPosts list whose size is always bigger than the original
                // list of posts i.e. #responsePosts because of the addition of header items too. Therefore, to access
                // a particular index/object in the #responsePosts, we must keep track of the current index using a
                // separate variable i.e. #postIndex
                for (int postIndex = 0, i = 1; i <= formattedPostsSize; ++i, ++postIndex) {
                    Post previousPost = postIndex > 0 ? responsePosts.get(postIndex - 1) : null;
                    Post post = responsePosts.get(postIndex);

                    boolean isNewDay = previousPost == null || mViewModel.isNewDay(previousPost, post);

                    // If we get a new day, we must add a header item to map it onto the header view in #PostsAdapter
                    if (isNewDay) {
                        formattedPosts.add(new PostHeader(PostUtils.INSTANCE.printDate(post.getDateObj())));
                        // Increment the #formattedPostsSize due to the addition of a header
                        ++formattedPostsSize;
                        // If the header is added, we must increment the 'i' again, one due to adding the header
                        // in #formattedPosts list and the other (3rd argument of the loop) due to adding the item
                        // in #formattedPosts list.
                        ++i;
                    }

                    formattedPosts.add(post);
                }

                hideLoadingAnimation();
                mapDataOnRecyclerView(formattedPosts);
            } else if (response.getStatus() == Status.ERROR) {
                // Tip: You can reproduce this issue by changing the URL to something wrong so that we're sure that
                // the error occurs
                hideLoadingAnimation();
                showErrorView(R.string.unexpected_error);
            }
        });
    }

    private void hideLoadingAnimation() {
        requireView().findViewById(R.id.animation_view).setVisibility(View.GONE);
    }

    private void mapDataOnRecyclerView(List<PostAdapterModel> posts) {
        RecyclerView recyclerView = requireView().findViewById(R.id.recycler_view);
        recyclerView.addItemDecoration(new VerticalItemDecoration(requireContext(), 1));
        recyclerView.setAdapter(new PostsAdapter(posts, mPostsRepository, this));
    }

    private void showErrorView(@StringRes int msg) {
        TextView errorText = requireView().findViewById(R.id.error_text);
        errorText.setVisibility(View.VISIBLE);
        errorText.setText(getString(msg));
    }

    @Override
    public void onPostClicked(@NonNull Post post) {
        Intent browseIntent = new Intent();
        browseIntent.setAction(Intent.ACTION_VIEW);
        browseIntent.setData(post.getUri());

        // Verifying if the link can be opened
        try {
            requireActivity().startActivity(browseIntent);
        } catch (ActivityNotFoundException e) {
            Toast.makeText(requireContext(), R.string.no_application_to_open_link, Toast.LENGTH_SHORT).show();
        }
    }
}