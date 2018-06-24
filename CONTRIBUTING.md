# Contributing

When contributing to this repository, please first discuss the change you wish to 
make via issue, email (swati4star@gmail.com) , or any other method with the owners 
of this repository before making a change. 

## Pull Request Process

### 1. Explore

If there is some issue or bug, you are interested in and no one else is working 
on the issue, you may take it up ( just leave a comment on the issue).

### 2. Fork & create a branch

If this is something you think you can fix, then create a branch with a 
descriptive name.

A good branch name would be (where issue #13 is the ticket you're working on):

```sh
git checkout -b 13-add-xyz-feature
```

### 3. Build & run the project locally

[Import the project](https://developer.android.com/studio/projects/create-project.html#ImportAProject) in Android Studio. 
Build it & run the project on emulator / real device.


### 4. Implement your fix or feature

At this point, you're ready to make your changes! Feel free to ask for help;
everyone is a beginner at first :smile_cat:


### 5. Test for all the checks

There should be no errors while running the following commands:

```sh
./gradlew assemble
./gradlew checkstyle
```
Your patch should follow the same conventions & pass the same code quality
checks as the rest of the project.

### 6. Make a Pull Request

At this point, you should switch back to your master branch and make sure it's
up to date with Active Admin's master branch:

```sh
git remote add upstream git@github.com:Swati4star/Images-to-PDF.git
git checkout master
git pull upstream master
```

Then update your feature branch from your local copy of master, and push it!

```sh
git checkout 13-add-xyz-feature
git rebase master
git push --set-upstream origin 13-add-xyz-feature
```

Finally, go to GitHub and [make a Pull Request][] :D

Travis CI and Circle CI will run our test suite. We care about quality, so 
your PR won't be merged until all tests pass. 

### 8. Squash the changes after review is done

We want one commit to represent one feature fix. So, the commits need to
be squashed before getting merge to master. You can check how to squash [here](https://github.com/todotxt/todo.txt-android/wiki/Squash-All-Commits-Related-to-a-Single-Issue-into-a-Single-Commit)


### Our Responsibilities

Project maintainers are responsible for clarifying the standards of acceptable
behavior and are expected to take appropriate and fair corrective action in
response to any instances of unacceptable behavior.

Project maintainers have the right and responsibility to remove, edit, or
reject comments, commits, code, wiki edits, issues, and other contributions
that are not aligned to this Code of Conduct, or to ban temporarily or
permanently any contributor for other behaviors that they deem inappropriate,
threatening, offensive, or harmful.
