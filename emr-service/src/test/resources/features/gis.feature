Feature:  Touch Point Login Feature
@WIP
Scenario: Touch point login event persisted

Given a valid touchpoint login event request
When the event is POSTed to the endpoint /touchpointevents
Then a touch point event will be persisted with login event details


Scenario: Touch point Query for latest login touchpoint event - No prior events

Given no previously saved login touchpoint event
When the userId is posted to the endpoint /touchpointevents/login
Then no latest login touchpoint event is returned

Scenario: Touch point Query for latest login touchpoint event - With one prior events

Given one previously saved login touchpoint event
When the userId is posted to the endpoint /touchpointevents/login
Then the latest login touchpoint event is returned

Scenario: Touch point Query for latest login touchpoint event - With multiple prior events

Given multiple previously saved login touchpoint event
When the userId is posted to the endpoint /touchpointevents/login
Then the latest login touchpoint event is returned

