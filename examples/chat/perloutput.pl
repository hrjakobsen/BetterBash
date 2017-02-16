#!/usr/bin/perl
use threads;

sub chathandler {
    my $Person1Count = 0;
    my $Person2Count = 0;
    open(STATUS, "./chat.sh |");
    while (<STATUS>) {
        if ($_ =~ "Person 1:") {
            $Person1Count++;
        } else {
            $Person2Count++;
        }
        printf("\nPerson 1: %d\nPerson 2: %d\n", $Person1Count, $Person2Count);
    }
    close STATUS;
}

#Start function in background
my $thr = threads->new(\&chathandler);

#Do other work
#...

#wait for thread to finish
$thr->join();